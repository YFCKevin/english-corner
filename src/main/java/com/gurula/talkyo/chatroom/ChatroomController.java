package com.gurula.talkyo.chatroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.azureai.dto.ChatAudioDTO;
import com.gurula.talkyo.chatroom.dto.*;
import com.gurula.talkyo.chatroom.enums.ChatroomType;
import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.chatroom.handler.MessageTypeHandler;
import com.gurula.talkyo.course.Lesson;
import com.gurula.talkyo.course.LessonRepository;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberContext;
import com.gurula.talkyo.member.MemberService;
import com.gurula.talkyo.openai.dto.LLMChatResponseDTO;
import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.record.LearningRecordService;
import com.gurula.talkyo.snapshot.SnapshotForm;
import com.gurula.talkyo.snapshot.SnapshotService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
public class ChatroomController {
    private final Logger logger = LoggerFactory.getLogger(ChatroomController.class);
    private final ChatroomService chatroomService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ConfigProperties configProperties;
    private final MessageTypeHandler handler;
    private final LearningRecordService learningRecordService;
    private final ConversationRepository conversationRepository;
    private final SimpleDateFormat sdf;
    private final MessageRepository messageRepository;
    private final ChatroomRepository chatroomRepository;
    private final LessonRepository lessonRepository;
    private final MessageService messageService;
    private final MemberService memberService;
    private final AudioService audioService;
    private final SnapshotService snapshotService;

    public ChatroomController(ChatroomService chatroomService, SimpMessageSendingOperations messagingTemplate, ConfigProperties configProperties, MessageTypeHandler handler, LearningRecordService learningRecordService,
                              ConversationRepository conversationRepository, SimpleDateFormat sdf,
                              MessageRepository messageRepository,
                              ChatroomRepository chatroomRepository,
                              LessonRepository lessonRepository, MessageService messageService, MemberService memberService, AudioService audioService, SnapshotService snapshotService) {
        this.chatroomService = chatroomService;
        this.messagingTemplate = messagingTemplate;
        this.configProperties = configProperties;
        this.handler = handler;
        this.learningRecordService = learningRecordService;
        this.conversationRepository = conversationRepository;
        this.sdf = sdf;
        this.messageRepository = messageRepository;
        this.chatroomRepository = chatroomRepository;
        this.lessonRepository = lessonRepository;
        this.messageService = messageService;
        this.memberService = memberService;
        this.audioService = audioService;
        this.snapshotService = snapshotService;
    }


    /**
     * 開啟新的聊天室
     * @param chatroomDTO
     * @return
     */
    @PostMapping("/chatroom/create")
    public ResponseEntity<?> create (@RequestBody ChatroomDTO chatroomDTO){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [createChatroom]", member.getName(), member.getId());

        final String chatroomId = chatroomService.createChatroom(member, chatroomDTO);

        ResultStatus<String> resultStatus = new ResultStatus<>();

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(chatroomId);

        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 初始化聊天室 (透過 chatroomId 取得歷史聊天記錄 or load Scenario and partner opening line)
     * @param chatInitDTO
     * @param headerAccessor
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    @MessageMapping("/init")
    public void init (@RequestBody ChatInitDTO chatInitDTO, SimpMessageHeaderAccessor headerAccessor) throws ExecutionException, InterruptedException, IOException {

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        Member member = (sessionAttributes != null) ? (Member) sessionAttributes.get("member") : null;

        if (member == null) {
            logger.warn("[chat init] 無法取得 Member，檢查 WebSocket 連線是否正確");
            return;
        }

        logger.info("[{} {}] [chat init]", member.getName(), member.getId());

        final String chatroomId = chatInitDTO.getChatroomId();

        final List<Map<Integer, Message>> messages = chatroomService.init(chatInitDTO, member);
        String chatroomDestination = "/chatroom/" + chatroomId;
        messagingTemplate.convertAndSend(chatroomDestination, new ConversationChainDTO(false, messages));
    }


    /**
     * 使用者上傳 audio file or image file
     * @param multipartFile
     * @param messageType
     * @return
     * @throws IOException
     */
    @PostMapping("/chatroom/upload")
    public ResponseEntity<?> upload (
            @RequestParam("multipartFile") MultipartFile multipartFile,
            @RequestParam("messageType") MessageType messageType,
            @RequestParam("chatroomId") String chatroomId
    ) throws IOException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [upload]", member.getName(), member.getId());

        ResultStatus<String> resultStatus = new ResultStatus<>();

        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setMultipartFile(multipartFile);
        chatDTO.setMessageType(messageType);
        chatDTO.setChatroomId(chatroomId);

        // 儲存音檔 or 圖片
        final String fileName = handler.saveMultipartFile(chatDTO, configProperties);

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");

        String filePath = "";
        switch (messageType) {
            case AUDIO -> filePath = configProperties.getAudioShowPath() + fileName;
            case IMAGE -> filePath = configProperties.getPicShowPath() + chatroomId + "/" + fileName;
        }

        resultStatus.setData(filePath);

        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 刪除上傳的檔案
     * @param fileRequestDTO
     * @return
     * @throws IOException
     */
    @DeleteMapping("/chatroom/file/delete")
    public ResponseEntity<?> fileDelete(@RequestBody FileRequestDTO fileRequestDTO) throws IOException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [file delete]", member.getName(), member.getId());

        ResultStatus<String> resultStatus = new ResultStatus<>();

        final MessageType messageType = fileRequestDTO.getMessageType();
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setChatroomId(fileRequestDTO.getChatroomId());
        chatDTO.setMessageType(messageType);
        if (MessageType.AUDIO.equals(messageType)) {
            chatDTO.setAudioFileName(fileRequestDTO.getFileName());
        } else if (MessageType.IMAGE.equals(messageType)) {
            chatDTO.setImageFileName(fileRequestDTO.getFileName());
        }

        try {
            handler.deleteFile(chatDTO, configProperties);
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
        } catch (Exception e) {
            resultStatus.setCode("C999");
            resultStatus.setMessage("失敗");
        }

        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 對話
     * @param chatDTO
     * @param headerAccessor
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    @MessageMapping("/chat")
    public void chat(@RequestBody ChatDTO chatDTO, SimpMessageHeaderAccessor headerAccessor) throws ExecutionException, InterruptedException, IOException {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        Member member = (sessionAttributes != null) ? (Member) sessionAttributes.get("member") : null;

        if (member == null) {
            logger.warn("[chat] 無法取得 Member，檢查 WebSocket 連線是否正確");
            return;
        }

        logger.info("[{} {}] [chat]", member.getName(), member.getId());

        final String chatroomId = chatDTO.getChatroomId();

        ConversationChainDTO conversationChainDTO = null;
        String chatroomDestination = "/chatroom/" + chatroomId;

        conversationChainDTO = chatroomService.handleHumanMsg(chatDTO, member);
        messagingTemplate.convertAndSend(chatroomDestination, conversationChainDTO);

        boolean hasMessage = conversationChainDTO.getMessages() != null &&
                conversationChainDTO.getMessages().stream()
                        .anyMatch(map -> map != null && !map.isEmpty());

        if (hasMessage) {
            String messageId = conversationChainDTO.getMessages().stream()
                    .flatMap(map -> map.values().stream())
                    .map(Message::getId)
                    .findFirst()
                    .orElse(null);

            if (messageId != null) {
                chatDTO.setMessageId(messageId);
            }
            conversationChainDTO = chatroomService.reply(chatDTO, member);

            messagingTemplate.convertAndSend(chatroomDestination, conversationChainDTO);
        }

    }


    /**
     * 產生進階語句
     * @param messageId
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/chatroom/advancedCheck/{messageId}")
    public ResponseEntity<?> advancedCheck(@PathVariable String messageId) throws IOException, ExecutionException, InterruptedException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [chatroom advancedCheck]", member.getName(), member.getId());

        ResultStatus<Message> resultStatus = new ResultStatus<>();

        try {
            final Optional<Message> opt = messageRepository.findById(messageId);
            if (opt.isPresent()) {
                Message message = opt.get();
                final Chatroom chatroom = chatroomRepository.findById(message.getChatroomId()).get();

                final CompletableFuture<ResultStatus<Void>> result = chatroomService.advancedCheck(new ChatRequestDTO(
                        chatroom.getChatroomType(),
                        messageId,
                        message.getBranch(),
                        message.getPreviewMessageId(),
                        member.getPartnerId()
                ));

                if ("C000".equals(result.get().getCode())) {
                    message = messageRepository.findById(messageId).get();
                    resultStatus.setCode("C000");
                    resultStatus.setMessage("成功");
                    resultStatus.setData(message);
                };

            } else {
                resultStatus.setCode("C001");
                resultStatus.setMessage("查無訊息");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            resultStatus.setCode("C999");
            resultStatus.setMessage("系統錯誤");
        }

        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 離開聊天室
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/chatroom/leave")
    public void leave(
            @RequestParam String chatroomId,
            @RequestParam ChatroomType chatroomType,
            @RequestParam(required = false) String lessonId,
            @RequestParam(required = false) String unitNumber,
            HttpServletResponse response
    ) throws IOException, ExecutionException, InterruptedException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [chatroom leave]", member.getName(), member.getId());

        switch (chatroomType) {
            case PROJECT -> {
                // generate learning report
                chatroomService.genLearningReport(chatroomId);

                // mark learning record finish
                learningRecordService.finish(chatroomId);

                memberService.addExp(member, 5);

                // member and partner leave chatroom
                conversationRepository.leaveChatroom(chatroomId, member.getId(), sdf.format(new Date()));
                conversationRepository.leaveChatroom(chatroomId, member.getPartnerId(), sdf.format(new Date()));

                // 導向測驗結束結果頁面
                System.out.println("導向測驗結束結果頁面");
                String redirectUrl = configProperties.getGlobalDomain() + "learning-report.html?chatroomId=" + URLEncoder.encode(chatroomId, StandardCharsets.UTF_8) +
                        "&lessonId=" + URLEncoder.encode(lessonId, StandardCharsets.UTF_8);
                response.setStatus(HttpServletResponse.SC_FOUND);   // 302
                response.setHeader("Location", redirectUrl);    // 設定 Location Header
            }
            case SITUATION -> {
                // generate learning report
                chatroomService.genLearningReport(chatroomId);

                // mark learning record finish
                learningRecordService.finish(chatroomId);

                memberService.addExp(member, 5);

                // member and partner leave chatroom
                conversationRepository.leaveChatroom(chatroomId, member.getId(), sdf.format(new Date()));
                conversationRepository.leaveChatroom(chatroomId, member.getPartnerId(), sdf.format(new Date()));

                // mark the chatroom as closed
                chatroomService.close(chatroomId);

                // 導向測驗結束結果頁面
                System.out.println("導向測驗結束結果頁面");
                String redirectUrl = configProperties.getGlobalDomain() + "learning-report.html?chatroomId=" + URLEncoder.encode(chatroomId, StandardCharsets.UTF_8) +
                        "&unitNumber=" + URLEncoder.encode(unitNumber, StandardCharsets.UTF_8);
                response.setStatus(HttpServletResponse.SC_FOUND);   // 302
                response.setHeader("Location", redirectUrl);    // 設定 Location Header
            }
        }
    }


    /**
     * 取得個人學習報告
     * @param chatroomId
     * @param lessonId
     * @return
     */
    @GetMapping("/chatroom/learningReport")
    public ResponseEntity<?> learningReport(
            @RequestParam String chatroomId,
            @RequestParam(required = false) String lessonId,
            @RequestParam(required = false) String unitNumber) throws IOException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [learningReport]", member.getName(), member.getId());

        ResultStatus<LearningReportDTO> resultStatus = new ResultStatus<>();

        LearningReport learningReport = chatroomService.getLearningReport(chatroomId);

        final ConversationScore conversationScore = learningReport.getConversationScore();
        final double prosody = conversationScore != null
                ? conversationScore.getProsody() : 0.0;
        final double fluency = conversationScore != null
                ? conversationScore.getFluency() : 0.0;
        final double completeness = conversationScore != null
                ? conversationScore.getCompleteness() : 0.0;
        final double accuracy = conversationScore != null
                ? conversationScore.getAccuracy() : 0.0;
        final double overallRating = (accuracy + completeness + fluency + prosody) / 4;

        String title = null;
        if (StringUtils.isNotBlank(lessonId)) {
            final Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
            if (lesson != null) {
                title = lesson.getName();
            }
        }

        if (StringUtils.isNotBlank(unitNumber) && StringUtils.isBlank(title)) {
            final Optional<ScenarioDTO> scenarioDTO = chatroomService.getScenarios().stream()
                    .filter(s -> unitNumber.equals(s.getUnitNumber()))
                    .findFirst();
            if (scenarioDTO.isPresent()) {
                title = scenarioDTO.get().getZhTitle();
            }
        }

        LearningReportDTO dto = new LearningReportDTO(
                title,
                conversationScore,
                learningReport.getFeedback(),
                overallRating
        );

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(dto);
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 預覽情境
     * @param lessonId
     * @return
     */
    @GetMapping("/chatroom/previewLesson/{lessonId}")
    public ResponseEntity<?> previewLesson(@PathVariable String lessonId){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [previewLesson]", member.getName(), member.getId());

        ResultStatus<Lesson> resultStatus = new ResultStatus<>();

        final Optional<Lesson> opt = lessonRepository.findById(lessonId);
        if (opt.isPresent()) {
            final Lesson lesson = opt.get();
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
            resultStatus.setData(lesson);
        }
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 產生引導提示句子
     * @param messageId
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/chatroom/guidingSentence/{messageId}")
    public ResponseEntity<?> guidingSentence(@PathVariable String messageId) throws JsonProcessingException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [guiding sentence]", member.getName(), member.getId());

        ResultStatus<LLMChatResponseDTO> resultStatus = new ResultStatus<>();

        LLMChatResponseDTO llmChatResponseDTO = chatroomService.genGuidingSentence(messageId);

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(llmChatResponseDTO);
        return ResponseEntity.ok(resultStatus);
    }



    @GetMapping("/chatroom/scenario-list")
    public ResponseEntity<?> scenarioList () throws IOException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [scenario list]", member.getName(), member.getId());

        ResultStatus<List<ScenarioDTO>> resultStatus = new ResultStatus<>();

        List<ScenarioDTO> scenarioDTOList = chatroomService.getScenarios();

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(scenarioDTOList);
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 變更 branch
     * @param previewMessageId
     * @param targetVersion
     * @return
     */
    @GetMapping("/chatroom/switchMsg/{previewMessageId}/{targetVersion}")
    public ResponseEntity<?> switchMsg (@PathVariable String previewMessageId, @PathVariable int targetVersion){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [switch message]", member.getName(), member.getId());

        ResultStatus<List<Map<Integer, Message>>> resultStatus = new ResultStatus<>();

        final List<Map<Integer, Message>> historyMsgs = messageService.getHistoryMessageWhenSwitchBranch(previewMessageId, targetVersion);

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(historyMsgs);
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 取得使用者所有 SITUATION 的歷史對話紀錄
     * @return
     */
    @GetMapping("/scenario/history/record")
    public ResponseEntity<?> scenarioHistoryRecord (){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [scenarioHistoryRecord]", member.getName(), member.getId());

        ResultStatus<List<ScenarioHistoryRecordDTO>> resultStatus = new ResultStatus<>();

        List<Chatroom> chatrooms = chatroomService.getScenarioHistoryRecord(member.getId());

        List<ScenarioHistoryRecordDTO> recordList = new ArrayList<>();
        chatrooms.forEach(chatroom -> {
            final ConversationScore conversationScore = chatroom.getReport().getConversationScore();
            final double prosody = conversationScore != null
                    ? conversationScore.getProsody() : 0.0;
            final double fluency = conversationScore != null
                    ? conversationScore.getFluency() : 0.0;
            final double completeness = conversationScore != null
                    ? conversationScore.getCompleteness() : 0.0;
            final double accuracy = conversationScore != null
                    ? conversationScore.getAccuracy() : 0.0;
            final double overallRating = (accuracy + completeness + fluency + prosody) / 4;
            ScenarioHistoryRecordDTO recordDTO = new ScenarioHistoryRecordDTO(
                    chatroom.getId(),
                    chatroom.getScenario().getUnitNumber(),
                    chatroom.getCloseDate(),
                    overallRating
            );
            recordList.add(recordDTO);
        });

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(recordList);
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 產生音檔
     * @param chatAudioDTO
     * @return
     */
    @PostMapping("/chatroom/genAudio")
    public ResponseEntity<?> genAudio (@RequestBody ChatAudioDTO chatAudioDTO) {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [genAudio]", member.getName(), member.getId());

        final String unitNumber = chatAudioDTO.getUnitNumber();

        chatAudioDTO.setPartnerId(member.getPartnerId());
        chatAudioDTO.setMemberId(member.getId());
        ResultStatus<String> resultStatus = new ResultStatus<>();
        try {
            final String fileName = audioService.textToSpeechAndPlaySound(chatAudioDTO);
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
            if (StringUtils.isBlank(unitNumber)) {
                resultStatus.setData(configProperties.getAudioShowPath() + "temp/" + fileName);
            } else {
                messageService.saveAudio(member, unitNumber, fileName);
                resultStatus.setData(configProperties.getAudioShowPath() + "favorite/" + fileName);
            }
        } catch (Exception e) {
            resultStatus.setCode("C999");
            resultStatus.setMessage("系統錯誤");
            logger.error(e.getMessage());
        }
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 取得 currentMessageId 用來回推根訊息
     * @param chatroomId
     * @return
     */
    @GetMapping("/chatroom/getCurrentMsgId/{chatroomId}")
    public ResponseEntity<?> getCurrentMsgId (@PathVariable String chatroomId){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [getCurrentMsgId]", member.getName(), member.getId());

        String currentMsgId = chatroomService.getCurrentMsgId(chatroomId);

        ResultStatus<String> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(currentMsgId);
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 產生聊天室標題
     */
    @PostMapping("/chatroom/title/generate")
    public ResponseEntity<?> genTitle (@RequestBody ChatDTO chatDTO) throws JsonProcessingException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [genTitle]", member.getName(), member.getId());

        final String chatroomId = chatDTO.getChatroomId();
        final Map<String, String> map = chatroomService.genTitle(chatroomId);

        ResultStatus<Map<String, String>> resultStatus = new ResultStatus<>();

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(map);

        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 取得歷史紀錄
     * @return
     */
    @GetMapping("/chatroom/history")
    public ResponseEntity<?> getHistories (){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [get history]", member.getName(), member.getId());

        Map<String, String> chatroomIdAndTitleMap = chatroomService.getChatroomHistory(member.getId());

        ResultStatus<Map<String, String>> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(chatroomIdAndTitleMap);
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * FREE_TALK 更換對話集
     * @param chatInitDTO
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping("/chatroom/enter")
    public ResponseEntity<?> enterChatroom (@RequestBody ChatInitDTO chatInitDTO) throws IOException, ExecutionException, InterruptedException {
        ResultStatus<List<Map<Integer, Message>>> resultStatus = new ResultStatus<>();

        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [enter chatroom]", member.getName(), member.getId());

        final List<Map<Integer, Message>> messages = chatroomService.init(chatInitDTO, member);

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(messages);
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 取得 FREE_TALK 的歷史對話紀錄
     * @param chatInitDTO
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping("/chatroom/history/message/all")
    public ResponseEntity<?> historyMessages (@RequestBody ChatInitDTO chatInitDTO) throws IOException, ExecutionException, InterruptedException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [enter chatroom]", member.getName(), member.getId());

        final List<Map<Integer, Message>> messages = chatroomService.init(chatInitDTO, member);

        ResultStatus<List<Map<Integer, Message>>> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(messages);
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 刪除歷史對話紀錄
     * @param chatroomId
     * @return
     */
    @DeleteMapping("/chatroom/chatRecord/delete/{chatroomId}")
    public ResponseEntity<?> deleteChatRecord(@PathVariable String chatroomId) {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [deleteChatRecord]", member.getName(), member.getId());

        ResultStatus<Void> resultStatus = new ResultStatus<>();

        int count = chatroomService.deleteChatRecord(chatroomId);

        if (count > 0) {
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
        } else {
            resultStatus.setCode("C999");
            resultStatus.setMessage("系統錯誤");
        }
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 重新命名
     * @param chatRecordDTO
     * @return
     */
    @PatchMapping("/chatroom/chatRecord/editName")
    public ResponseEntity<?> editName(@RequestBody ChatRecordDTO chatRecordDTO) {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [editName chatRecord]", member.getName(), member.getId());

        ResultStatus<Void> resultStatus = new ResultStatus<>();

        int count = chatroomService.editName(chatRecordDTO);

        if (count > 0) {
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
        } else {
            resultStatus.setCode("C999");
            resultStatus.setMessage("系統錯誤");
        }
        return ResponseEntity.ok(resultStatus);
    }


    @GetMapping("/share/{link}")
    public ResponseEntity<Void> redirectToChatroom(@PathVariable String link) {
        logger.info("[open share link]");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(configProperties.getGlobalDomain() + "chatroom.html?closed=true&link=" + link))
                .build();
    }


    /**
     * 刪除訊息(PROJECT & SITUATION)
     * @param messageStr
     * @return
     */
    @DeleteMapping("/chatroom/message/delete/{messageStr}")
    public ResponseEntity<?> messageDelete(@PathVariable String messageStr) throws IOException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [delete message]", member.getName(), member.getId());

        ResultStatus<Void> resultStatus = new ResultStatus<>();

        final String[] messageIds = messageStr.split("_");
        messageService.deleteMessage(messageIds);
        resultStatus.setCode("C000");
        return ResponseEntity.ok(resultStatus);
    }


    /**
     * 每日早上5點定時清空 audio/temp 內的音檔
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void cleanTempDirectory() {
        String tempDirectory = configProperties.getAudioSavePath() + "temp";
        File tempDir = new File(tempDirectory);

        if (!tempDir.exists() || !tempDir.isDirectory()) {
            logger.warn("Temp directory does not exist: {}", tempDirectory);
            return;
        }

        File[] files = tempDir.listFiles();
        if (files == null || files.length == 0) {
            logger.info("No files to clean in temp directory.");
            return;
        }

        for (File file : files) {
            try {
                Files.deleteIfExists(file.toPath());
                logger.info("Deleted file: {}", file.getName());
            } catch (Exception e) {
                logger.error("Failed to delete file: {}", file.getName(), e);
            }
        }

        logger.info("Temp directory cleanup completed.");
    }
}
