package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.dto.*;
import com.gurula.talkyo.chatroom.enums.ChatroomType;
import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.chatroom.handler.MessageTypeHandler;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberContext;
import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.record.LearningRecordService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public ChatroomController(ChatroomService chatroomService, SimpMessageSendingOperations messagingTemplate, ConfigProperties configProperties, MessageTypeHandler handler, LearningRecordService learningRecordService,
                              ConversationRepository conversationRepository, SimpleDateFormat sdf,
                              MessageRepository messageRepository,
                              ChatroomRepository chatroomRepository) {
        this.chatroomService = chatroomService;
        this.messagingTemplate = messagingTemplate;
        this.configProperties = configProperties;
        this.handler = handler;
        this.learningRecordService = learningRecordService;
        this.conversationRepository = conversationRepository;
        this.sdf = sdf;
        this.messageRepository = messageRepository;
        this.chatroomRepository = chatroomRepository;
    }

    @PostMapping("/chatroom/createChatroom")
    public ResponseEntity<?> createChatroom (@RequestBody ChatroomDTO chatroomDTO){
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
            case IMAGE -> filePath = configProperties.getPicShowPath() + fileName;
        }

        resultStatus.setData(filePath);

        return ResponseEntity.ok(resultStatus);
    }



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
        final ChatroomType chatroomType = chatDTO.getChatroomType();

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
            MessageResponseDTO dto = new MessageResponseDTO();

            messagingTemplate.convertAndSend(chatroomDestination, conversationChainDTO);
        }

    }


    @GetMapping("/chatroom/advancedCheck/{messageId}")
    public ResponseEntity<?> advancedCheck(@PathVariable String messageId) throws IOException, ExecutionException, InterruptedException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [chatroom advancedCheck]", member.getName(), member.getId());

        ResultStatus resultStatus = new ResultStatus();

        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final Message message = opt.get();
            final Chatroom chatroom = chatroomRepository.findById(message.getChatroomId()).get();

            chatroomService.advancedCheck(new ChatRequestDTO(
                    messageId,
                    chatroom.getChatroomType(),
                    message.getBranch(),
                    message.getPreviewMessageId()
            ));
        }

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        return ResponseEntity.ok(resultStatus);
    }


    @PostMapping("/chatroom/leave")
    public void leave (@RequestBody ChatDTO chatDTO) throws IOException, ExecutionException, InterruptedException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [chatroom leave]", member.getName(), member.getId());

        final String chatroomId = chatDTO.getChatroomId();
        final ChatroomType chatroomType = chatDTO.getChatroomType();

        switch (chatroomType) {
            case PROJECT -> {
                // generate learning report
                chatroomService.genLearningReport(chatroomId);

                // mark learning record finish
                learningRecordService.finish(chatroomId);

                // member and partner leave chatroom
                conversationRepository.leaveChatroom(chatroomId, member.getId(), sdf.format(new Date()));
                conversationRepository.leaveChatroom(chatroomId, member.getPartnerId(), sdf.format(new Date()));

                // 導向測驗結束結果頁面
                System.out.println("導向測驗結束結果頁面");
            }
            case SITUATION -> {

                // member and partner leave chatroom
                conversationRepository.leaveChatroom(chatroomId, member.getId(), sdf.format(new Date()));
                conversationRepository.leaveChatroom(chatroomId, member.getPartnerId(), sdf.format(new Date()));

                // mark the chatroom as closed
                chatroomService.close(chatroomId);

                // 導向首頁
                System.out.println("導向首頁");
            }
        }
    }
}
