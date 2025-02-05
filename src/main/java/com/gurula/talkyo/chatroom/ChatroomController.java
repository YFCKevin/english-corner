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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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

    public ChatroomController(ChatroomService chatroomService, SimpMessageSendingOperations messagingTemplate, ConfigProperties configProperties, MessageTypeHandler handler, LearningRecordService learningRecordService,
                              ConversationRepository conversationRepository, SimpleDateFormat sdf) {
        this.chatroomService = chatroomService;
        this.messagingTemplate = messagingTemplate;
        this.configProperties = configProperties;
        this.handler = handler;
        this.learningRecordService = learningRecordService;
        this.conversationRepository = conversationRepository;
        this.sdf = sdf;
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

        chatroomService.init(chatInitDTO, member);
    }


    /**
     * 使用者上傳 audio file or image file
     * @param multipartFile
     * @param messageType
     * @return
     * @throws IOException
     */
    @PostMapping("/chatroom/upload")
    public ResponseEntity<ResultStatus<Map<String, String>>> uploadAudio (
            @RequestParam("multipartFile") MultipartFile multipartFile,
            @RequestParam("messageType") MessageType messageType,
            @RequestParam("chatroomId") String chatroomId
    ) throws IOException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [upload]", member.getName(), member.getId());

        ResultStatus<Map<String, String>> resultStatus = new ResultStatus<>();

        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setMultipartFile(multipartFile);
        chatDTO.setMessageType(messageType);
        chatDTO.setChatroomId(chatroomId);

        // 儲存音檔 or 圖片
        final String fileName = handler.saveMultipartFile(chatDTO, configProperties);

        if (Files.size(Paths.get(configProperties.getAudioSavePath(), chatroomId, fileName)) > 0) {  // 代表檔案儲存成功
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
            resultStatus.setData(Map.of(chatDTO.getMessageType().toString(), fileName));
        }

        return ResponseEntity.ok(resultStatus);
    }

    //TODO: 刪除檔案


    @MessageMapping("/chat")
    public void chat (@RequestBody ChatDTO chatDTO, SimpMessageHeaderAccessor headerAccessor) throws ExecutionException, InterruptedException, IOException {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        Member member = (sessionAttributes != null) ? (Member) sessionAttributes.get("member") : null;

        if (member == null) {
            logger.warn("[chat] 無法取得 Member，檢查 WebSocket 連線是否正確");
            return;
        }

        logger.info("[{} {}] [chat]", member.getName(), member.getId());

        final String chatroomId = chatDTO.getChatroomId();
        final ChatroomType chatroomType = chatDTO.getChatroomType();

        ConversationChainDTO conversationChainDTO = chatroomService.reply(chatDTO, member);
        String chatroomDestination = "/chatroom/" + chatroomId + "/" + chatroomType;
        messagingTemplate.convertAndSend(chatroomDestination, conversationChainDTO);
    }


    @PostMapping("/chatroom/end")
    public void end (@RequestBody ChatDTO chatDTO) throws IOException, ExecutionException, InterruptedException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [conversation end]", member.getName(), member.getId());

        // generate learning report
        chatroomService.genLearningReport(chatDTO);

        // mark learning record finish
        learningRecordService.finish(chatDTO.getChatroomId());

        // member and partner leave chatroom
        final String chatroomId = chatDTO.getChatroomId();
        conversationRepository.leaveChatroom(chatroomId, member.getId(), sdf.format(new Date()));
        conversationRepository.leaveChatroom(chatroomId, member.getPartnerId(), sdf.format(new Date()));

        if (StringUtils.isNotBlank(chatDTO.getLessonId())) {
            // 導向測驗結束結果頁面
            System.out.println("導向測驗結束結果頁面");
        } else {
            // 導向首頁
            System.out.println("導向首頁");
        }
    }
}
