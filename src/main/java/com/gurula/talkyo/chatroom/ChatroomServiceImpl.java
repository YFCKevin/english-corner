package com.gurula.talkyo.chatroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.azureai.dto.ChatAudioDTO;
import com.gurula.talkyo.chatroom.dto.*;
import com.gurula.talkyo.chatroom.enums.ChatroomType;
import com.gurula.talkyo.chatroom.enums.RoomStatus;
import com.gurula.talkyo.chatroom.enums.SenderRole;
import com.gurula.talkyo.chatroom.utils.AudioUtil;
import com.gurula.talkyo.config.RabbitMQConfig;
import com.gurula.talkyo.course.Lesson;
import com.gurula.talkyo.course.LessonRepository;
import com.gurula.talkyo.course.Sentence;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.openai.LLMService;
import com.gurula.talkyo.openai.dto.AdvanceSentencesResponseDTO;
import com.gurula.talkyo.openai.dto.GrammarResponseDTO;
import com.gurula.talkyo.openai.dto.LLMChatRequestDTO;
import com.gurula.talkyo.openai.dto.LLMChatResponseDTO;
import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.record.LearningRecordService;
import com.gurula.talkyo.record.dto.RecordDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ChatroomServiceImpl implements ChatroomService {
    private final ChatroomRepository chatroomRepository;
    private final SimpleDateFormat sdf;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AudioService audioService;
    private final ConfigProperties configProperties;
    private final LLMService llmService;
    private final RabbitTemplate rabbitTemplate;
    private final LessonRepository lessonRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final LearningRecordService learningRecordService;
    private final Map<String, List<CompletableFuture<Void>>> chattingFutures = new ConcurrentHashMap<>();

    public ChatroomServiceImpl(ChatroomRepository chatroomRepository, @Qualifier("sdf") SimpleDateFormat sdf,
                               ConversationRepository conversationRepository, MessageRepository messageRepository, AudioService audioService, ConfigProperties configProperties, LLMService llmService, RabbitTemplate rabbitTemplate, LessonRepository lessonRepository, SimpMessageSendingOperations messagingTemplate, LearningRecordService learningRecordService) {
        this.chatroomRepository = chatroomRepository;
        this.sdf = sdf;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.audioService = audioService;
        this.configProperties = configProperties;
        this.llmService = llmService;
        this.rabbitTemplate = rabbitTemplate;
        this.lessonRepository = lessonRepository;
        this.messagingTemplate = messagingTemplate;
        this.learningRecordService = learningRecordService;
    }

    @Transactional
    @Override
    public String createChatroom(Member member, ChatroomDTO chatroomDTO) {
        final ChatroomType chatroomType = chatroomDTO.getChatroomType();
        List<Chatroom> chatrooms = chatroomRepository.findByOwnerIdAndChatroomTypeAndRoomStatusOrderByCreationDateDesc(member.getId(), chatroomType, RoomStatus.ACTIVE);
        System.out.println("chatrooms.size() = " + chatrooms.size());
        switch (chatroomType) {
            case PROJECT -> {
                if (chatrooms.size() > 0) {
                    // 已有聊天室，則回傳chatroomId
                    return chatrooms.get(0).getId();
                } else {
                    // 創建新的學習課程聊天室
                    Chatroom chatroom = new Chatroom();
                    chatroom.setOwnerId(member.getId());
                    chatroom.setChatroomType(chatroomType);
                    chatroom.setRoomStatus(RoomStatus.ACTIVE);
                    chatroom.setCreationDate(sdf.format(new Date()));
                    final Chatroom savedChatroom = chatroomRepository.save(chatroom);

                    // 紀錄 member 進入聊天室的紀錄
                    Conversation conversation = new Conversation()
                            .enterChatroom(member.getId(), savedChatroom.getId());

                    conversationRepository.save(conversation);
                    return savedChatroom.getId();
                }
            }
        }
        return null;
    }

    @Transactional
    @Override
    public void init(ChatInitDTO chatInitDTO, Member member) throws IOException, ExecutionException, InterruptedException {

        final String chatroomId = chatInitDTO.getChatroomId();
        final String courseId = chatInitDTO.getCourseId();
        final String lessonId = chatInitDTO.getLessonId();
        final String memberId = member.getId();
        final Scenario scenario = chatInitDTO.getScenario();
        final String partnerId = member.getPartnerId();
        final ChatroomType chatroomType = chatInitDTO.getChatroomType();

        boolean exists = messageRepository.existsByChatroomId(chatroomId);
        if (exists) {    // 代表聊天室已有聊天記錄
            // 取聊天記錄
            final List<Message> historyMsgs = messageRepository.findAllByChatroomIdOrderByCreatedDateTimeAsc(chatroomId);
            System.out.println("取聊天記錄");
            String chatroomDestination = "/chatroom/" + chatroomId + "/" + chatroomType;
            messagingTemplate.convertAndSend(chatroomDestination, new ConversationChainDTO(historyMsgs));
        } else {
            switch (chatroomType) {
                case PROJECT -> {
                    // load scenario
                    loadScenario(new ChatRequestDTO(chatroomId, chatroomType, scenario));

                    // partner opening line
                    final String openingLineMessage = openingLine(new ChatRequestDTO(chatroomId, scenario, memberId, partnerId, lessonId));

                    // learning record
                    learningRecordService.saveRecord(new RecordDTO(courseId, lessonId, chatroomId), memberId);

                    // show opening line
                    messageRepository.findById(openingLineMessage).ifPresent(finalMessage -> {
                        String chatroomDestination = "/chatroom/" + chatroomId + "/" + chatroomType;
                        messagingTemplate.convertAndSend(chatroomDestination, new ConversationChainDTO(List.of(finalMessage)));
                    });
                }
            }
        }

    }


    /**
     * 將場景 scenario 存入 chatroom
     * @param chatRequestDTO
     */
    @Override
    public void loadScenario(ChatRequestDTO chatRequestDTO) {
        System.out.println("load scenario");

        final String chatroomId = chatRequestDTO.getChatroomId();
        final Scenario scenario = chatRequestDTO.getScenario();
        final ChatroomType chatroomType = chatRequestDTO.getChatroomType();
        final Chatroom chatroom = chatroomRepository.findById(chatroomId).get();
        if (ChatroomType.PROJECT.equals(chatroomType)) {
            chatroom.setScenario(scenario);
        } else if (ChatroomType.SITUATION.equals(chatroomType)) {
            chatroom.setScenario(scenario);
        }
        chatroomRepository.save(chatroom);
    }


    /**
     * 產生 partner 開場白
     * @param chatRequestDTO
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public String openingLine(ChatRequestDTO chatRequestDTO) throws IOException, ExecutionException, InterruptedException {
        System.out.println("opening line");

        final String chatroomId = chatRequestDTO.getChatroomId();
        final String partnerId = chatRequestDTO.getPartnerId();
        final String lessonId = chatRequestDTO.getLessonId();
        final Scenario scenario = chatRequestDTO.getScenario();
        final String memberId = chatRequestDTO.getMemberId();

        // partner 參與聊天室紀錄
        Conversation conversation = new Conversation()
                .enterChatroom(partnerId, chatroomId);
        conversationRepository.save(conversation);

        LLMChatResponseDTO llmChatResponseDTO = new LLMChatResponseDTO();

        if (StringUtils.isNotBlank(lessonId)) { // 學習
            final Optional<Lesson> opt = lessonRepository.findById(lessonId);
            if (opt.isPresent()) {
                final Lesson lesson = opt.get();
                final String sentenceStr = lesson.getSentences().stream()
                        .map(Sentence::getContent)
                        .collect(Collectors.joining("\n"));
                llmChatResponseDTO = llmService.genWelcomeMessage(new LLMChatRequestDTO(scenario, sentenceStr));
            }
        } else {
            llmChatResponseDTO = llmService.genWelcomeMessage(new LLMChatRequestDTO(scenario));
        }

        final Message message = new Message();
        message.setTranslation(llmChatResponseDTO.getTranslation());
        message.setCreatedDateTime(sdf.format(new Date()));
        message.setChatroomId(chatroomId);
        message.setSender(partnerId);
        message.setSenderRole(SenderRole.AI);
        final String fileName = audioService.textToSpeechInChatting (List.of(new ChatAudioDTO(llmChatResponseDTO.getContent(), memberId, partnerId, chatroomId))).get(0).getFileName().toString();
        message.setAudioName(fileName);
        message.setSize(Files.size(Paths.get(configProperties.getAudioSavePath(), chatroomId, fileName)));
        message.setParsedText(llmChatResponseDTO.getContent());
        final Message savedMessage = messageRepository.save(message);
        return savedMessage.getId();
    }


    /**
     * 使用者回應訊息，觸發 audio to text
     * @param chatDTO
     * @param member
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public ConversationChainDTO reply(ChatDTO chatDTO, Member member) throws ExecutionException, InterruptedException, IOException {
        Message message = new Message();
        final String chatroomId = chatDTO.getChatroomId();
        final String audioFileName = chatDTO.getAudioFileName();
        final String imageFileName = chatDTO.getImageFileName();
        final String content = chatDTO.getContent();    // 文字輸入
        if (StringUtils.isNotBlank(audioFileName)) {
            message.setAudioName(audioFileName);
            message.setSize(Files.size(Paths.get(configProperties.getAudioSavePath(), chatroomId, audioFileName)));
        } else if (StringUtils.isNotBlank(imageFileName)) {
            message.setImageName(imageFileName);
            message.setSize(Files.size(Paths.get(configProperties.getPicSavePath(), chatroomId, imageFileName)));
        }
        message.setChatroomId(chatroomId);
        message.setSender(member.getId());
        message.setSenderRole(SenderRole.HUMAN);
        message.setCreatedDateTime(sdf.format(new Date()));
        if (StringUtils.isNotBlank(content)) {
            message.setText(content);
        }
        final Message savedMessage = messageRepository.save(message);

        ConversationChainDTO conversationChainDTO = null;
        final ChatRequestDTO chatRequestDTO = new ChatRequestDTO(
                chatroomId,
                member.getId(),
                member.getPartnerId(),
                savedMessage.getId(),
                chatDTO.getLessonId()
        );

        // speechToText
        CompletableFuture<Void> future = speechToText(chatRequestDTO);
        future.get();

        final Message finalMsg = messageRepository.findById(savedMessage.getId()).get();
        conversationChainDTO = new ConversationChainDTO(List.of(finalMsg));

        return conversationChainDTO;
    }


    /**
     * audio to text 完成後，觸發文法校正、取得進階語句、夥伴回應、發音分析
     * @param chatRequestDTO
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public CompletableFuture<Void> speechToText(ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException {
        System.out.println("speechToText");

        final String messageId = chatRequestDTO.getMessageId();
        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final Message message = opt.get();

            String audioFilePath = Paths.get(configProperties.getAudioSavePath(), message.getChatroomId(), message.getAudioName()).toString();
            String recognitionText = audioService.speechToText(audioFilePath);
            chatRequestDTO.setAudioFilePath(audioFilePath);
            chatRequestDTO.setReferenceText(recognitionText);
            message.setParsedText(recognitionText);
            messageRepository.save(message);
            System.out.println("speechToText儲存成功");

            System.out.println("執行 [文法校正、取得進階語句、夥伴回應、發音分析] 攜帶的參數：" + chatRequestDTO);

            // 呼叫廣播，執行文法校正、取得進階語句、夥伴回應、發音分析
            CompletableFuture<Void> grammarFuture = new CompletableFuture<>();
            CompletableFuture<Void> advancedFuture = new CompletableFuture<>();
            CompletableFuture<Void> partnerFuture = new CompletableFuture<>();
            CompletableFuture<Void> pronunciationFuture = new CompletableFuture<>();

            chattingFutures.put(chatRequestDTO.getMessageId(), new ArrayList<>(List.of(partnerFuture, grammarFuture, advancedFuture, pronunciationFuture)));

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TALKYO_CHAT_FANOUT_EXCHANGE,
                    "",
                    chatRequestDTO
            );

            // 等待所有 Queue 完成
            return CompletableFuture.allOf(partnerFuture, grammarFuture, advancedFuture, pronunciationFuture);

        }
        return CompletableFuture.completedFuture(null);
    }


    /**
     * 文法校正
     * @param chatRequestDTO
     * @throws JsonProcessingException
     */
    @Override
    @RabbitListener(queues = RabbitMQConfig.GRAMMAR_QUEUE)
    public void grammarCheck(ChatRequestDTO chatRequestDTO) throws JsonProcessingException {
        System.out.println("GrammarCheck");

        final String messageId = chatRequestDTO.getMessageId();
        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final Message message = opt.get();
            String grammarInputText = "";

            if (StringUtils.isNotBlank(message.getParsedText())) {
                grammarInputText = message.getParsedText();
            } else if (StringUtils.isNotBlank(message.getText())) {
                grammarInputText = message.getText();
            }

            GrammarResponseDTO grammarResponseDTO = llmService.grammarCheck(grammarInputText);

            GrammarResult grammarResult = new GrammarResult();

            if (StringUtils.isNotBlank(grammarResponseDTO.getErrorReason())) {  // 文法有錯誤
                grammarResult.setCorrectSentence(grammarResponseDTO.getCorrectSentence());
                grammarResult.setErrorReason(grammarResponseDTO.getErrorReason());
                grammarResult.setTranslation(grammarResponseDTO.getTranslation());
                grammarResult.setErrorSentence(grammarInputText);
            } else {    // 文法正確
                messageRepository.updateAccuracy(messageId, true);
                grammarResult.setCorrectSentence(grammarResponseDTO.getCorrectSentence());
                grammarResult.setTranslation(grammarResponseDTO.getTranslation());
            }

            message.setGrammarResult(grammarResult);
            messageRepository.updateGrammarResult(messageId, grammarResult);
            System.out.println("GrammarCheck成功");
        }
    }


    /**
     * 進階語句
     * @param chatRequestDTO
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws JsonProcessingException
     */
    @Override
    @RabbitListener(queues = RabbitMQConfig.ADVANCED_SENTENCE_QUEUE)
    public void genAdvanceSentences(ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, JsonProcessingException {
        System.out.println("advancedSentence");

        final String messageId = chatRequestDTO.getMessageId();
        final String partnerId = chatRequestDTO.getPartnerId();
        final String chatroomId = chatRequestDTO.getChatroomId();

        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final Message message = opt.get();
            String content = StringUtils.isNotBlank(message.getParsedText()) ? message.getParsedText() : message.getText();
            List<AdvanceSentencesResponseDTO> advanceSentencesResponseDTOS = llmService.advanceSentences(content)
                    .stream()
                    // 非正式的句子排在前面
                    .sorted(Comparator.comparing(AdvanceSentencesResponseDTO::isFormal))
                    .toList();
            final List<Path> audioFilePaths = audioService.textToSpeechInChatting(Arrays.asList(
                    new ChatAudioDTO(advanceSentencesResponseDTOS.get(0).getSentence(), partnerId, chatroomId),
                    new ChatAudioDTO(advanceSentencesResponseDTOS.get(1).getSentence(), partnerId, chatroomId)
            ));
            Map<String, Path> audioMap = Map.of("informal", audioFilePaths.get(0), "formal", audioFilePaths.get(1));
            List<AdvancedSentence> sentences = new ArrayList<>();
            advanceSentencesResponseDTOS.forEach(s -> {
                AdvancedSentence advancedSentence = new AdvancedSentence();
                advancedSentence.setExplanation(s.getExplanation());
                advancedSentence.setContent(s.getSentence());
                advancedSentence.setFormal(s.isFormal());
                if (s.isFormal()) { // formal
                    advancedSentence.setAudioName(List.of(audioMap.get("formal").getFileName().toString()));
                } else {    // informal
                    advancedSentence.setAudioName(List.of(audioMap.get("informal").getFileName().toString()));
                }
                sentences.add(advancedSentence);
            });

            message.setAdvancedSentences(sentences);
            messageRepository.updateAdvancedSentences(messageId, sentences);
            System.out.println("advancedSentence成功");
        }
    }


    /**
     * partner 回應
     * @param chatRequestDTO
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    @RabbitListener(queues = RabbitMQConfig.PARTNER_REPLY_QUEUE)
    public void partnerReply(ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, IOException {
        System.out.println("partnerReply");

        final String chatroomId = chatRequestDTO.getChatroomId();
        final String memberId = chatRequestDTO.getMemberId();
        final String partnerId = chatRequestDTO.getPartnerId();

        final Scenario scenario = chatroomRepository.findById(chatroomId).get().getScenario();

        LLMChatResponseDTO llmChatResponseDTO = new LLMChatResponseDTO();

        List<Message> messages = messageRepository.findAllByChatroomIdOrderByCreatedDateTimeAsc(chatroomId);
        final String historyMessages = messages.stream()
                .map(message -> Optional.ofNullable(message.getParsedText()).orElse(message.getText()))
                .collect(Collectors.joining("\n"));

        llmChatResponseDTO = llmService.replyMsg(new LLMChatRequestDTO(historyMessages, scenario));
        final Message message = new Message();
        message.setTranslation(llmChatResponseDTO.getTranslation());
        message.setChatroomId(chatroomId);
        message.setCreatedDateTime(sdf.format(new Date()));
        message.setSender(partnerId);
        message.setSenderRole(SenderRole.AI);
        final String fileName = audioService.textToSpeechInChatting(List.of(new ChatAudioDTO(llmChatResponseDTO.getContent(), memberId, partnerId, chatroomId))).get(0).getFileName().toString();
        message.setAudioName(fileName);
        message.setSize(Files.size(Paths.get(configProperties.getAudioSavePath(), chatroomId, fileName)));
        message.setParsedText(llmChatResponseDTO.getContent());
        messageRepository.save(message);
        System.out.println("partnerReply成功");
    }


    /**
     * 發音分析
     * @param chatRequestDTO
     * @throws InterruptedException
     * @throws JsonProcessingException
     * @throws ExecutionException
     */
    @Override
    @RabbitListener(queues = RabbitMQConfig.PRONUNCIATION_QUEUE)
    public void analyzePronunciation(ChatRequestDTO chatRequestDTO) {
        System.out.println("pronunciation");

        final String messageId = chatRequestDTO.getMessageId();
        final String referenceText = chatRequestDTO.getReferenceText();
        final String audioFilePath = chatRequestDTO.getAudioFilePath();

        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final ConversationScore conversationScore = audioService.pronunciation(referenceText, audioFilePath);
            final Message message = opt.get();
            message.setConversationScore(conversationScore);
            messageRepository.updateConversationScore(messageId, conversationScore);
            System.out.println("pronunciation儲存成功");
        }
    }


    /**
     * 產生學習報告
     * @param chatDTO
     * @throws IOException
     */
    @Override
    @Transactional
    public void genLearningReport(ChatDTO chatDTO) throws IOException {

        System.out.println("genLearningReport");

        final String chatroomId = chatDTO.getChatroomId();
        System.out.println("chatroomId = " + chatroomId);
        final List<Message> messages = messageRepository.findAllByChatroomIdOrderByCreatedDateTimeAsc(chatroomId);

        // 組裝 reference text
        final String referenceText = messages.stream()
                .filter(message -> message.getSenderRole().equals(SenderRole.HUMAN))
                .map(Message::getParsedText)
                .collect(Collectors.joining("\n"));

        // 取得使用者的語音對話檔
        final List<String> audioFilePaths = messages.stream()
                .filter(message -> message.getSenderRole().equals(SenderRole.HUMAN) &&
                        StringUtils.isNotBlank(message.getParsedText()) &&
                        StringUtils.isNotBlank(message.getAudioName())
                )
                .map(message -> Paths.get(configProperties.getAudioSavePath(), chatroomId, message.getAudioName()).toString())
                .toList();

        final String mergeFilePath = Paths.get(
                configProperties.getAudioSavePath(),
                chatroomId, "merge", System.currentTimeMillis() + ".wav"
        ).toString();
        AudioUtil.mergeAudioFiles(mergeFilePath, audioFilePaths);

        final String dialogueText = messages.stream()
                .map(message -> {
                    String prefix = "";
                    if (SenderRole.AI.equals(message.getSenderRole())) {
                        prefix = "partner: ";
                    } else if (SenderRole.HUMAN.equals(message.getSenderRole())) {
                        prefix = "user: ";
                    }
                    String content = StringUtils.defaultIfBlank(message.getParsedText(), message.getText());
                    return prefix + content;
                })
                .collect(Collectors.joining("\n"));

        final ReportRequestDTO reportRequestDTO =
                new ReportRequestDTO(referenceText, mergeFilePath, dialogueText);

        CompletableFuture<ConversationScore> conversationFuture = CompletableFuture.supplyAsync(() ->
                pronunciationResult(reportRequestDTO)
        );

        CompletableFuture<ContentAssessment> contentFuture = CompletableFuture.supplyAsync(() ->
                topicResult(reportRequestDTO)
        );

        CompletableFuture<Feedback> feedbackFuture = CompletableFuture.supplyAsync(() ->
                {
                    try {
                        return feedback(reportRequestDTO);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        CompletableFuture.allOf(conversationFuture, contentFuture, feedbackFuture).join();

        ConversationScore conversationScore = conversationFuture.join();
        ContentAssessment contentAssessment = contentFuture.join();
        Feedback feedback = feedbackFuture.join();

        conversationScore.setContentAssessment(contentAssessment);

        final Optional<Chatroom> chatroomOpt = chatroomRepository.findById(chatroomId);
        if (chatroomOpt.isPresent()) {
            final Chatroom chatroom = chatroomOpt.get();
            chatroom.setRoomStatus(RoomStatus.CLOSED);
            chatroom.setCloseDate(sdf.format(new Date()));
            chatroom.setReport(new LearningReport(conversationScore, feedback));
            chatroomRepository.save(chatroom);
        }

    }


    /**
     * 學習報告 -- 全文發音分析
     * @param reportRequestDTO
     * @return
     */
    @Override
    public ConversationScore pronunciationResult(ReportRequestDTO reportRequestDTO) {
        System.out.println("pronunciationResult");
        final String referenceText = reportRequestDTO.getReferenceText();
        final String destinationFilePath = reportRequestDTO.getDestinationFilePath();
        final ConversationScore pronunciation = audioService.pronunciation(referenceText, destinationFilePath);
        System.out.println("pronunciationResult完成");
        return pronunciation;
    }


    /**
     * 主題分析
     * @param reportRequestDTO
     * @return
     */
    @Override
    public ContentAssessment topicResult(ReportRequestDTO reportRequestDTO) {
        System.out.println("topicResult");
        final String referenceText = reportRequestDTO.getReferenceText();
        final String destinationFilePath = reportRequestDTO.getDestinationFilePath();
        final ContentAssessment contentAssessment = audioService.analyzeTopic(referenceText, destinationFilePath);
        System.out.println("topicResult完成");
        return contentAssessment;
    }


    /**
     * AI 回饋
     * @param reportRequestDTO
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public Feedback feedback(ReportRequestDTO reportRequestDTO) throws JsonProcessingException {
        System.out.println("feedback");
        final String dialogueText = reportRequestDTO.getDialogueText();
        final Feedback feedback = llmService.feedback(dialogueText);
        System.out.println("feedback完成");
        return feedback;
    }
}
