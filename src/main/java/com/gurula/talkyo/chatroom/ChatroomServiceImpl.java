package com.gurula.talkyo.chatroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.azureai.dto.ChatAudioDTO;
import com.gurula.talkyo.chatroom.dto.*;
import com.gurula.talkyo.chatroom.enums.ActionType;
import com.gurula.talkyo.chatroom.enums.ChatroomType;
import com.gurula.talkyo.chatroom.enums.RoomStatus;
import com.gurula.talkyo.chatroom.enums.SenderRole;
import com.gurula.talkyo.chatroom.utils.AudioUtil;
import com.gurula.talkyo.chatroom.utils.ConfigurationUtil;
import com.gurula.talkyo.config.RabbitMQConfig;
import com.gurula.talkyo.course.Lesson;
import com.gurula.talkyo.course.LessonRepository;
import com.gurula.talkyo.course.Sentence;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.gemini.ImageAnalysisService;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.openai.LLMService;
import com.gurula.talkyo.openai.dto.AdvanceSentencesResponseDTO;
import com.gurula.talkyo.openai.dto.GrammarResponseDTO;
import com.gurula.talkyo.openai.dto.LLMChatRequestDTO;
import com.gurula.talkyo.openai.dto.LLMChatResponseDTO;
import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.record.LearningRecord;
import com.gurula.talkyo.record.LearningRecordRepository;
import com.gurula.talkyo.record.LearningRecordService;
import com.gurula.talkyo.record.dto.RecordDTO;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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
    private final ImageAnalysisService imageAnalysisService;
    private final LearningRecordService learningRecordService;
    private final MessageService messageService;
    private final Map<String, List<CompletableFuture<Void>>> chattingFutures = new ConcurrentHashMap<>();
    private final LearningRecordRepository learningRecordRepository;

    public ChatroomServiceImpl(ChatroomRepository chatroomRepository, @Qualifier("sdf") SimpleDateFormat sdf,
                               ConversationRepository conversationRepository, MessageRepository messageRepository, AudioService audioService, ConfigProperties configProperties, LLMService llmService, RabbitTemplate rabbitTemplate, LessonRepository lessonRepository, ImageAnalysisService imageAnalysisService, LearningRecordService learningRecordService, MessageService messageService,
                               LearningRecordRepository learningRecordRepository) {
        this.chatroomRepository = chatroomRepository;
        this.sdf = sdf;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.audioService = audioService;
        this.configProperties = configProperties;
        this.llmService = llmService;
        this.rabbitTemplate = rabbitTemplate;
        this.lessonRepository = lessonRepository;
        this.imageAnalysisService = imageAnalysisService;
        this.learningRecordService = learningRecordService;
        this.messageService = messageService;
        this.learningRecordRepository = learningRecordRepository;
    }

    @Transactional
    @Override
    public String createChatroom(Member member, ChatroomDTO chatroomDTO) {

        final ChatroomType chatroomType = chatroomDTO.getChatroomType();
        final ActionType action = chatroomDTO.getAction();
        final String lessonId = chatroomDTO.getLessonId();
        final String unitNumber = chatroomDTO.getUnitNumber();

        List<Chatroom> chatrooms = chatroomRepository.findByOwnerIdAndChatroomTypeAndRoomStatusOrderByCreationDateDesc(member.getId(), chatroomType, RoomStatus.ACTIVE);
        final Set<LearningRecord> learningRecords = learningRecordRepository.findByChatroomIdIn(chatrooms.stream().map(Chatroom::getId).collect(Collectors.toSet()));
        final Set<String> lessonIds = learningRecords.stream()
                .map(LearningRecord::getLessonId)
                .collect(Collectors.toSet());
        final boolean targetLessonExisted = lessonIds.contains(lessonId);
        Map<String, String> lessonChatroomMap = learningRecords.stream()
                .collect(Collectors.toMap(
                        LearningRecord::getLessonId,
                        LearningRecord::getChatroomId,
                        (existingValue, newValue) -> newValue
                ));


        switch (chatroomType) {
            case PROJECT -> {
                if (chatrooms.size() > 0 && targetLessonExisted) {
                    // 已有聊天室，則回傳chatroomId
                    return lessonChatroomMap.get(lessonId);
                } else {
                    // 創建新的聊天室
                    return createChatroom(member, chatroomType);
                }
            }
            case SITUATION -> {
                // 創建新的聊天室
                return createChatroom(member, chatroomType);
            }
            case FREE_TALK -> {
                switch (action) {
                    case LEGACY -> {
                        if (chatrooms.size() > 0) {
                            // 已有聊天室，比對unitNumber，則回傳最新的chatroomId
                            return chatrooms.stream()
                                    .filter(chatroom -> unitNumber.equals(chatroom.getScenario().getUnitNumber()))
                                    .map(Chatroom::getId)
                                    .findFirst()
                                    .orElseGet(() -> createChatroom(member, chatroomType));
                        } else {
                            // 創建新的聊天室
                            return createChatroom(member, chatroomType);
                        }
                    }
                    case CREATE -> {
                        // 創建新的聊天室
                        return createChatroom(member, chatroomType);
                    }
                }
            }
        }
        return null;
    }

    private String createChatroom(Member member, ChatroomType chatroomType) {
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

    @Transactional
    @Override
    public List<Map<Integer, Message>> init(ChatInitDTO chatInitDTO, Member member) throws IOException, ExecutionException, InterruptedException {

        final String chatroomId = chatInitDTO.getChatroomId();
        String courseId = chatInitDTO.getCourseId();
        final String lessonId = chatInitDTO.getLessonId();
        final String memberId = member.getId();
        final String partnerId = member.getPartnerId();
        final ChatroomType chatroomType = chatInitDTO.getChatroomType();
        final String currentMessageId = chatInitDTO.getCurrentMessageId();
        final String unitNumber = chatInitDTO.getUnitNumber();

        boolean exists = messageRepository.existsByChatroomId(chatroomId);
        if (exists) {    // 代表聊天室已有聊天記錄
            // 取聊天記錄
            System.out.println("取聊天記錄");
            List<Map<Integer, Message>> historyMsgs = new ArrayList<>();

            switch (chatroomType) {
                case PROJECT, SITUATION -> {
                    final List<Message> messages = messageRepository.findAllByChatroomIdOrderByCreatedDateTimeAsc(chatroomId);
                    for (Message message : messages) {
                        historyMsgs.add(Map.of(1, message));
                    }
                }
                case FREE_TALK -> {
                    if (StringUtils.isNotBlank(currentMessageId)) { // 有歷史聊天記錄
                        historyMsgs = messageService.getHistoryMessages(currentMessageId);
                    }
                }
            }

            System.out.println("聊天記錄====> " + historyMsgs);

            return historyMsgs;
        } else {    // 無聊天記錄，產生一則開場白
            System.out.println("無聊天記錄，產生一則開場白");
            String openingLineMessage = "";
            switch (chatroomType) {
                case PROJECT -> {
                    final Lesson lesson = lessonRepository.findById(lessonId).get();
                    courseId = lesson.getCourseId();

                    // load scenario
                    final Scenario scenario = lesson.getScenario();
                    loadScenario(new ChatRequestDTO(chatroomId, chatroomType, scenario));

                    // partner 參與聊天室紀錄
                    Conversation conversation = new Conversation()
                            .enterChatroom(partnerId, chatroomId);
                    conversationRepository.save(conversation);

                    // partner opening line
                    openingLineMessage = openingLine(new ChatRequestDTO(chatroomId, scenario, memberId, partnerId, lessonId, chatroomType));

                    // learning record
                    learningRecordService.saveRecord(new RecordDTO(courseId, lessonId, chatroomId), memberId);
                }
                case SITUATION -> {
                    final List<ScenarioDTO> scenarios = getScenarios();
                    final ScenarioDTO scenarioDTO = scenarios.stream()
                            .filter(s -> unitNumber.equals(s.getUnitNumber())).findFirst().get();
                    Scenario scenario = new Scenario(
                            unitNumber,
                            scenarioDTO.getHumanRole(),
                            scenarioDTO.getPartnerRole(),
                            scenarioDTO.getSubject()
                    );

                    // load scenario
                    loadScenario(new ChatRequestDTO(chatroomId, chatroomType, scenario));

                    // partner 參與聊天室紀錄
                    Conversation conversation = new Conversation()
                            .enterChatroom(partnerId, chatroomId);
                    conversationRepository.save(conversation);

                    // partner opening line
                    openingLineMessage = openingLine(new ChatRequestDTO(chatroomId, scenario, memberId, partnerId, lessonId, chatroomType));

                    // learning record
                    learningRecordService.saveRecord(new RecordDTO(courseId, lessonId, chatroomId), memberId);
                }
                case FREE_TALK -> {
                    final List<ScenarioDTO> scenarios = getScenarios();
                    final ScenarioDTO scenarioDTO = scenarios.stream()
                            .filter(s -> unitNumber.equals(s.getUnitNumber())).findFirst().get();
                    Scenario scenario = new Scenario(
                            unitNumber,
                            scenarioDTO.getHumanRole(),
                            scenarioDTO.getPartnerRole(),
                            scenarioDTO.getSubject()
                    );

                    // load scenario
                    loadScenario(new ChatRequestDTO(chatroomId, chatroomType, scenario));

                    // partner 參與聊天室紀錄
                    Conversation conversation = new Conversation()
                            .enterChatroom(partnerId, chatroomId);
                    conversationRepository.save(conversation);

                    // partner opening line
                    openingLineMessage = openingLine(new ChatRequestDTO(chatroomId, scenario, memberId, partnerId, lessonId, chatroomType));
                }
            }

            // show opening line
            final Optional<Message> opt = messageRepository.findById(openingLineMessage);
            if (opt.isPresent()) {
                final Message finalMessage = opt.get();
                return List.of(Map.of(1, finalMessage));
            } else {
                return List.of(Map.of(1, new Message()));
            }
        }

    }


    /**
     * 將場景 scenario 存入 chatroom
     *
     * @param chatRequestDTO
     */
    @Override
    public void loadScenario(ChatRequestDTO chatRequestDTO) {
        System.out.println("load scenario");

        final String chatroomId = chatRequestDTO.getChatroomId();
        final Scenario scenario = chatRequestDTO.getScenario();
        final Chatroom chatroom = chatroomRepository.findById(chatroomId).get();
        chatroom.setScenario(scenario);
        chatroomRepository.save(chatroom);
    }


    /**
     * 產生 partner 開場白
     *
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
        final ChatroomType chatroomType = chatRequestDTO.getChatroomType();

        LLMChatResponseDTO llmChatResponseDTO = new LLMChatResponseDTO();

        switch (chatroomType) {
            case PROJECT -> {
                final Optional<Lesson> opt = lessonRepository.findById(lessonId);
                if (opt.isPresent()) {
                    final Lesson lesson = opt.get();
                    final String sentenceStr = lesson.getSentences().stream()
                            .map(Sentence::getContent)
                            .collect(Collectors.joining("\n"));
                    llmChatResponseDTO = llmService.genWelcomeMessage(new LLMChatRequestDTO(scenario, sentenceStr));
                }
            }
            case SITUATION -> llmChatResponseDTO = llmService.genWelcomeMessage(new LLMChatRequestDTO(scenario));
            case FREE_TALK -> {
                Message message = new Message();
                message.setCreatedDateTime(sdf.format(new Date()));
                message.setChatroomId(chatroomId);
                message.setSender(partnerId);
                message.setSenderRole(SenderRole.AI);
                llmChatResponseDTO = llmService.genWelcomeMessage(new LLMChatRequestDTO(scenario));
                final String fileName = audioService.textToSpeechInChatting(List.of(new ChatAudioDTO(llmChatResponseDTO.getContent(), memberId, partnerId, chatroomId))).get(0).getFileName().toString();
                message.setAudioName(fileName);
                message.setSize(Files.size(Paths.get(configProperties.getAudioSavePath(), chatroomId, fileName)));
                message.setParsedText(llmChatResponseDTO.getContent());
                message.setTranslation(llmChatResponseDTO.getTranslation());
                message.setBranch(UUID.randomUUID().toString());
                message.setVersion(1);
                final Message savedMessage = messageRepository.save(message);
                return savedMessage.getId();
            }
        }

        return saveMessage(chatroomId, partnerId, memberId, llmChatResponseDTO);
    }

    private String saveMessage(String chatroomId, String partnerId, String memberId, LLMChatResponseDTO llmChatResponseDTO) throws ExecutionException, InterruptedException, IOException {
        final Message message = new Message();
        message.setTranslation(llmChatResponseDTO.getTranslation());
        message.setCreatedDateTime(sdf.format(new Date()));
        message.setChatroomId(chatroomId);
        message.setSender(partnerId);
        message.setSenderRole(SenderRole.AI);
        final String fileName = audioService.textToSpeechInChatting(List.of(new ChatAudioDTO(llmChatResponseDTO.getContent(), memberId, partnerId, chatroomId))).get(0).getFileName().toString();
        message.setAudioName(fileName);
        message.setSize(Files.size(Paths.get(configProperties.getAudioSavePath(), chatroomId, fileName)));
        message.setParsedText(llmChatResponseDTO.getContent());
        final Message savedMessage = messageRepository.save(message);
        return savedMessage.getId();
    }


    @Override
    public ConversationChainDTO handleHumanMsg(ChatDTO chatDTO, Member member) throws IOException, ExecutionException, InterruptedException {

        final String memberId = member.getId();
        final String chatroomId = chatDTO.getChatroomId();
        final String audioFileName = chatDTO.getAudioFileName();
        final String imageFileName = chatDTO.getImageFileName();
        final String content = chatDTO.getContent();    // 文字輸入
        final ChatroomType chatroomType = chatDTO.getChatroomType();
        final String previewMessageId = chatDTO.getPreviewMessageId();
        final ActionType action = chatDTO.getAction();

        Message message = new Message();

        CompletableFuture<String> speechToTextFuture = null;
        CompletableFuture<String> imageAnalysisFuture = null;

        if (StringUtils.isNotBlank(audioFileName)) {
            message.setAudioName(audioFileName);
            message.setSize(Files.size(Paths.get(configProperties.getAudioSavePath(), chatroomId, audioFileName)));

            SpeechToTextDTO speechToTextDTO = new SpeechToTextDTO(
                    chatroomId,
                    content,
                    audioFileName
            );
            speechToTextFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return speechToText(speechToTextDTO);
                } catch (ExecutionException | IOException | InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }

        if (StringUtils.isNotBlank(imageFileName)) {
            message.setImageName(imageFileName);
            final Path imagePath = Paths.get(configProperties.getPicSavePath(), chatroomId, imageFileName);
            message.setSize(Files.size(imagePath));

            imageAnalysisFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return imageAnalysisService.imageAnalysis(imagePath.toString(), chatDTO.getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }

        if (speechToTextFuture != null) {
            message.setParsedText(speechToTextFuture.join());
        }
        if (imageAnalysisFuture != null) {
            message.setImageDesc(imageAnalysisFuture.join());
        }

        if (StringUtils.isNotBlank(content)) {
            message.setText(content);
        }

        message.setChatroomId(chatroomId);
        message.setSender(memberId);
        message.setSenderRole(SenderRole.HUMAN);
        message.setCreatedDateTime(sdf.format(new Date()));

        switch (chatroomType) {
            case PROJECT, SITUATION -> {
                message = messageRepository.save(message);
            }
            case FREE_TALK -> {
                switch (action) {
                    case EDIT -> { // 開立新分支並新增訊息 (編輯)
                        System.out.println("開立新分支並新增訊息 (編輯)");
                        int maxVersion = 1;
                        List<Message> messages = messageRepository.findByPreviewMessageId(previewMessageId);

                        maxVersion = messages.stream()
                                .mapToInt(Message::getVersion)
                                .max()
                                .orElse(1);

                        message.setPreviewMessageId(previewMessageId);
                        message.setBranch(UUID.randomUUID().toString());
                        message.setVersion(maxVersion + 1);
                    }
                    case CREATE -> { // 在該分支新增、在舊分支新增
                        System.out.println("在該分支新增、在舊分支新增");
                        final Message previewMessage = messageRepository.findById(previewMessageId).get();
                        message.setPreviewMessageId(previewMessageId);
                        message.setBranch(previewMessage.getBranch());
                        message.setVersion(1);
                    }
                }
                message = messageRepository.save(message);

                return new ConversationChainDTO(true, List.of(Map.of(message.getVersion(), message)));
            }
        }

        return new ConversationChainDTO(true, List.of(Map.of(1, message)));
    }


    /**
     * 使用者回應訊息，觸發 audio to text
     *
     * @param chatDTO
     * @param member
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public ConversationChainDTO reply(ChatDTO chatDTO, Member member) throws ExecutionException, InterruptedException, IOException {

        final String chatroomId = chatDTO.getChatroomId();
        final String lessonId = chatDTO.getLessonId();
        final ChatroomType chatroomType = chatDTO.getChatroomType();
        final String previewMessageId = chatDTO.getPreviewMessageId();
        final String messageId = chatDTO.getMessageId();    // human msg id

        final Message message = messageRepository.findById(messageId).get();

        final ChatRequestDTO chatRequestDTO = new ChatRequestDTO(
                chatroomId,
                member.getId(),
                member.getPartnerId(),
                messageId,
                lessonId,
                chatroomType,
                message.getBranch(),
                previewMessageId
        );

        Message partnerReplyMsg = partnerReply(chatRequestDTO);

        return new ConversationChainDTO(false, List.of(Map.of(1, partnerReplyMsg)));
    }


    /**
     * audio to text 完成後，觸發文法校正、取得進階語句、夥伴回應、發音分析
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public String speechToText(SpeechToTextDTO speechToTextDTO) throws ExecutionException, InterruptedException, IOException {
        System.out.println("speechToText");

        if (StringUtils.isNotBlank(speechToTextDTO.getText())) {
            return null;
        }

        String audioFilePath = Paths.get(configProperties.getAudioSavePath(), speechToTextDTO.getChatroomId(), speechToTextDTO.getAudioName()).toString();
        String recognitionText = audioService.speechToText(audioFilePath);
        System.out.println("speechToText儲存成功");
        return recognitionText;
    }

    @Override
    public CompletableFuture<ResultStatus<Void>> advancedCheck(ChatRequestDTO chatRequestDTO) throws IOException, ExecutionException, InterruptedException {

        final ChatroomType chatroomType = chatRequestDTO.getChatroomType();
        final String messageId = chatRequestDTO.getMessageId();
        final String branch = chatRequestDTO.getBranch();
        final String previewMessageId = chatRequestDTO.getPreviewMessageId();

        final Message message = messageRepository.findById(messageId).get();

        switch (chatroomType) {
            case PROJECT -> {
                System.out.println("執行 [文法校正、取得進階語句、夥伴回應、發音分析] 攜帶的參數：" + chatRequestDTO);

                chatRequestDTO.setChatroomId(message.getChatroomId());
                chatRequestDTO.setReferenceText(message.getParsedText());
                chatRequestDTO.setAudioFilePath(configProperties.getAudioSavePath() + message.getChatroomId() + "/" + message.getAudioName());

                // 呼叫廣播，執行文法校正、取得進階語句、夥伴回應、發音分析
                CompletableFuture<Void> grammarFuture = new CompletableFuture<>();
                CompletableFuture<Void> advancedFuture = new CompletableFuture<>();
                CompletableFuture<Void> pronunciationFuture = new CompletableFuture<>();

                chattingFutures.put(messageId, new ArrayList<>(List.of(grammarFuture, advancedFuture, pronunciationFuture)));

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.TALKYO_PROJECT_FANOUT_EXCHANGE,
                        "",
                        chatRequestDTO
                );

                // 等待所有 Queue 完成
                return CompletableFuture.allOf(grammarFuture, advancedFuture, pronunciationFuture)
                        .thenApply(v -> {
                            ResultStatus<Void> resultStatus = new ResultStatus<>();
                            resultStatus.setCode("C000");
                            return resultStatus;
                        });
            }
            case SITUATION -> {
                System.out.println("執行 [文法校正、夥伴回應] 攜帶的參數：" + chatRequestDTO);

                CompletableFuture<Void> grammarFuture = new CompletableFuture<>();

                chattingFutures.put(messageId, new ArrayList<>(List.of(grammarFuture)));

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.TALKYO_SITUATION_FANOUT_EXCHANGE,
                        "",
                        chatRequestDTO
                );

                // 等待所有 Queue 完成
                return CompletableFuture.allOf(grammarFuture).thenApply(v -> {
                    ResultStatus<Void> resultStatus = new ResultStatus<>();
                    resultStatus.setCode("C000");
                    return resultStatus;
                });
            }
//            case FREE_TALK -> {
//                message.setBranch(branch);
//                message.setPreviewMessageId(previewMessageId);
//                messageRepository.save(message);
//
//                return CompletableFuture.completedFuture(null).thenApply(v -> {
//                    ResultStatus<Void> resultStatus = new ResultStatus<>();
//                    resultStatus.setCode("C000");
//                    return resultStatus;
//                });
//            }
        }

        return CompletableFuture.completedFuture(null);
    }


    /**
     * 文法校正
     *
     * @param chatRequestDTO
     * @throws JsonProcessingException
     */
    @Override
    @RabbitListener(queues = RabbitMQConfig.GRAMMAR_QUEUE)
    public void grammarCheck(ChatRequestDTO chatRequestDTO) throws JsonProcessingException {
        System.out.println("GrammarCheck");

        final String messageId = chatRequestDTO.getMessageId();
        final String chatroomId = chatRequestDTO.getChatroomId();

        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final Message message = opt.get();
            String currentMsgContent = "";

            if (StringUtils.isNotBlank(message.getParsedText())) {
                currentMsgContent = message.getParsedText();
            } else if (StringUtils.isNotBlank(message.getText())) {
                currentMsgContent = message.getText();
            }

            GrammarResponseDTO grammarResponseDTO = null;

            final Optional<Message> previewMsgOpt = messageRepository.findFirstByChatroomIdAndCreatedDateTimeLessThanOrderByCreatedDateTimeDesc(chatroomId, message.getCreatedDateTime());
            if (previewMsgOpt.isPresent()) {
                final Message previewMsg = previewMsgOpt.get();
                String previewMsgContent = "";
                if (StringUtils.isNotBlank(previewMsg.getParsedText())) {
                    previewMsgContent = previewMsg.getParsedText();
                } else if (StringUtils.isNotBlank(previewMsg.getText())) {
                    previewMsgContent = previewMsg.getText();
                }
                grammarResponseDTO = llmService.grammarCheck(currentMsgContent, previewMsgContent);
            } else {
                grammarResponseDTO = llmService.grammarCheck(currentMsgContent, null);
            }


            GrammarResult grammarResult = new GrammarResult();

            if (StringUtils.isNotBlank(grammarResponseDTO.getErrorReason())) {  // 文法有錯誤
                grammarResult.setCorrectSentence(grammarResponseDTO.getCorrectSentence());
                grammarResult.setErrorReason(grammarResponseDTO.getErrorReason());
                grammarResult.setTranslation(grammarResponseDTO.getTranslation());
                grammarResult.setErrorSentence(currentMsgContent);
                message.setGrammarResult(grammarResult);
                messageRepository.updateGrammarResult(messageId, grammarResult);
            } else {    // 文法正確
                messageRepository.updateAccuracy(messageId, true);
            }

            System.out.println("GrammarCheck成功");
        }

        if (chattingFutures.containsKey(messageId)) {
            List<CompletableFuture<Void>> futures = chattingFutures.get(messageId);
            if (futures.size() >= 3) {
                futures.get(0).complete(null);
            }
        }
    }


    /**
     * 進階語句
     *
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

        if (chattingFutures.containsKey(chatRequestDTO.getMessageId())) {
            List<CompletableFuture<Void>> futures = chattingFutures.get(chatRequestDTO.getMessageId());
            if (futures.size() >= 3) {
                futures.get(1).complete(null);
            }
        }
    }


    /**
     * partner 回應
     *
     * @param chatRequestDTO
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public Message partnerReply(ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, IOException {
        System.out.println("partnerReply");

        final String chatroomId = chatRequestDTO.getChatroomId();
        final String memberId = chatRequestDTO.getMemberId();
        final String partnerId = chatRequestDTO.getPartnerId();
        final String messageId = chatRequestDTO.getMessageId();     // human message
        final String branch = chatRequestDTO.getBranch();
        final ChatroomType chatroomType = chatRequestDTO.getChatroomType();

        final Scenario scenario = chatroomRepository.findById(chatroomId).get().getScenario();

        LLMChatResponseDTO llmChatResponseDTO = new LLMChatResponseDTO();

        Message replyMsg = null;

        switch (chatroomType) {
            case PROJECT, SITUATION -> {
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
                replyMsg = messageRepository.save(message);
            }
            case FREE_TALK -> {
                List<Message> historyMsgs = messageService.getHistoryMessages(messageId).stream()
                        .flatMap(map -> map.values().stream())
                        .toList();

                final String historyMessages = historyMsgs.stream()
                        .map(message -> {
                            final String parsedText = message.getParsedText();
                            final String text = message.getText();
                            final String imageName = message.getImageName();
                            final String imageDesc = message.getImageDesc();
                            if (StringUtils.isNotBlank(parsedText)) return parsedText;
                            if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(imageName)) return text + "\n" + imageDesc;
                            return Optional.ofNullable(text).orElse(imageDesc);
                        })
                        .filter(Objects::nonNull)
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
                message.setVersion(1);
                message.setBranch(branch);
                message.setPreviewMessageId(messageId);
                replyMsg = messageRepository.save(message);
            }
        }

        System.out.println("partnerReply成功");
        return replyMsg;
    }


    /**
     * 發音分析
     *
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

        // 不是音檔則會進入
        if (StringUtils.isBlank(audioFilePath)) {
            if (chattingFutures.containsKey(messageId)) {
                List<CompletableFuture<Void>> futures = chattingFutures.get(messageId);
                if (futures.size() >= 3) {
                    futures.get(2).complete(null);
                }
            }
            return;
        }

        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final ConversationScore conversationScore = audioService.pronunciation(referenceText, audioFilePath);
            final Message message = opt.get();
            message.setConversationScore(conversationScore);
            messageRepository.updateConversationScore(messageId, conversationScore);
            System.out.println("pronunciation儲存成功");
        }

        if (chattingFutures.containsKey(chatRequestDTO.getMessageId())) {
            List<CompletableFuture<Void>> futures = chattingFutures.get(chatRequestDTO.getMessageId());
            if (futures.size() >= 3) {
                futures.get(2).complete(null);
            }
        }
    }


    /**
     * 產生學習報告
     *
     * @throws IOException
     */
    @Override
    @Transactional
    public void genLearningReport(String chatroomId) throws IOException {

        System.out.println("genLearningReport");

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
                new ReportRequestDTO(referenceText, dialogueText);

        CompletableFuture<ConversationScore> conversationFuture = CompletableFuture.completedFuture(null);
        CompletableFuture<ContentAssessment> contentFuture = CompletableFuture.completedFuture(null);

        if (!audioFilePaths.isEmpty()) {
            final String mergeFilePath = Paths.get(configProperties.getAudioSavePath(), chatroomId, "merge", System.currentTimeMillis() + ".wav").toString();
            AudioUtil.mergeAudioFiles(mergeFilePath, audioFilePaths);
            reportRequestDTO.setDestinationFilePath(mergeFilePath);

            conversationFuture = CompletableFuture.supplyAsync(() -> pronunciationResult(reportRequestDTO));
            contentFuture = CompletableFuture.supplyAsync(() -> topicResult(reportRequestDTO));
        }

        CompletableFuture<Feedback> feedbackFuture = CompletableFuture.supplyAsync(() ->
                {
                    try {
                        return feedback(reportRequestDTO);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        List<CompletableFuture<?>> futures = new ArrayList<>();
        if (!audioFilePaths.isEmpty()) {
            futures.add(conversationFuture);
            futures.add(contentFuture);
        }
        futures.add(feedbackFuture);

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        ConversationScore conversationScore = conversationFuture.join();
        ContentAssessment contentAssessment = contentFuture.join();
        Feedback feedback = feedbackFuture.join();

        if (conversationScore != null) {
            conversationScore.setContentAssessment(contentAssessment);
        }

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
     *
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
     *
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

    @Override
    public void close(String chatroomId) {
        System.out.println("chatroom closed");
        final Optional<Chatroom> opt = chatroomRepository.findById(chatroomId);
        if (opt.isPresent()) {
            final Chatroom chatroom = opt.get();
            chatroom.setCloseDate(sdf.format(new Date()));
            chatroom.setRoomStatus(RoomStatus.CLOSED);
            chatroomRepository.save(chatroom);
        }
    }


    /**
     * AI 回饋
     *
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


    @Override
    public LearningReport getLearningReport(String chatroomId) {
        final Optional<Chatroom> opt = chatroomRepository.findById(chatroomId);
        if (opt.isPresent()) {
            final Chatroom chatroom = opt.get();
            return chatroom.getReport();
        } else {
            return new LearningReport();
        }
    }


    @Override
    public LLMChatResponseDTO genGuidingSentence(String messageId) throws JsonProcessingException {
        final Optional<Message> msgOpt = messageRepository.findById(messageId);
        if (msgOpt.isPresent()) {
            final Message message = msgOpt.get();
            final String chatroomId = message.getChatroomId();
            final String partnerAskMsg = message.getParsedText();
            final Optional<Chatroom> chatroomOpt = chatroomRepository.findById(chatroomId);
            if (chatroomOpt.isPresent()) {
                final Chatroom chatroom = chatroomOpt.get();
                final Scenario scenario = chatroom.getScenario();
                LLMChatRequestDTO llmChatRequestDTO = new LLMChatRequestDTO(
                        partnerAskMsg,
                        scenario
                );
                return llmService.genGuidingSentence(llmChatRequestDTO);
            }
        }
        return new LLMChatResponseDTO();
    }

    @Override
    public List<ScenarioDTO> getScenarios() throws IOException {
        ConfigurationUtil.Configuration();
        File file = new File(configProperties.getJsonPath() + "scenario.json");
        TypeRef<List<ScenarioDTO>> typeRefCourse = new TypeRef<>() {
        };
        return JsonPath.parse(file).read("$", typeRefCourse);
    }

    @Override
    public List<Chatroom> getScenarioHistoryRecord(String memberId) {
        return chatroomRepository.findByOwnerIdAndChatroomTypeAndRoomStatusOrderByCloseDateAsc(memberId, ChatroomType.SITUATION, RoomStatus.CLOSED);
    }

    @Override
    public String getCurrentMsgId(String chatroomId) {
        Optional<Message> messageOpt = messageRepository.findByChatroomIdAndCurrentLastMsg(chatroomId, true);
        if (messageOpt.isPresent()) {
            return messageOpt.get().getId();
        } else {
            List<Message> messages = messageRepository.findAllByChatroomIdOrderByCreatedDateTimeAsc(chatroomId);
            if (messages.size() > 0) {
                return messages.get(messages.size() - 1).getId();
            } else {
                return null;
            }
        }
    }

    @Override
    public Map<String, String> genTitle(String chatroomId) throws JsonProcessingException {
        final List<Message> messages = messageRepository.findAllByChatroomIdOrderByCreatedDateTimeAsc(chatroomId);
        final String dialogueText = messages.stream()
                .map(message -> StringUtils.isNotBlank(message.getParsedText()) ? message.getParsedText() : message.getText())
                .collect(Collectors.joining("\n"));
        final String title = llmService.genChatroomTitle(dialogueText);
        final Chatroom chatroom = chatroomRepository.findById(chatroomId).get();
        chatroom.setTitle(title);
        chatroomRepository.save(chatroom);
        Map<String, String> map = new HashMap<>();
        map.put(chatroom.getId() + "_" + chatroom.getScenario().getUnitNumber(), title);
        return map;
    }

    @Override
    public Map<String, String> getChatroomHistory(String memberId) {
        final List<Chatroom> chatrooms = chatroomRepository.findByOwnerIdAndChatroomType(memberId, ChatroomType.FREE_TALK);

        return chatrooms.stream()
                .sorted(Comparator.comparing(Chatroom::getCreationDate).reversed())
                .collect(Collectors.toMap(
                        chatroom -> chatroom.getId() + "_" + chatroom.getScenario().getUnitNumber(),
                        chatroom -> StringUtils.isNotBlank(chatroom.getTitle())
                                ? chatroom.getTitle()
                                : chatroom.getCreationDate().toString(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    @Override
    public int deleteChatRecord(String chatroomId) {
        return chatroomRepository.deleteChatroomById(chatroomId);
    }

    @Override
    public int editName(ChatRecordDTO chatRecordDTO) {
        final Optional<Chatroom> opt = chatroomRepository.findById(chatRecordDTO.getChatroomId());
        if (opt.isPresent()) {
            final Chatroom chatroom = opt.get();
            chatroom.setTitle(chatRecordDTO.getEditName());
            chatroomRepository.save(chatroom);
            return 1;
        } else {
            return 0;
        }
    }
}
