package com.gurula.talkyo.chatroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.chatroom.dto.ChatDTO;
import com.gurula.talkyo.chatroom.dto.ChatInitDTO;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.enums.ConversationType;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import com.gurula.talkyo.chatroom.factory.ConversationFactory;
import com.gurula.talkyo.chatroom.handler.MessageTypeHandler;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberContext;
import com.gurula.talkyo.properties.ConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class ChatroomController {
    private final Logger logger = LoggerFactory.getLogger(ChatroomController.class);
    private final ChatroomService chatroomService;
    private final ConversationFactory conversationFactory;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ConfigProperties configProperties;
    static final Map<String, AbstractConversation> activeConversations = new HashMap<>();
    private final MessageTypeHandler handler;

    public ChatroomController(ChatroomService chatroomService, ConversationFactory conversationFactory, SimpMessageSendingOperations messagingTemplate, ConfigProperties configProperties, MessageTypeHandler handler) {
        this.chatroomService = chatroomService;
        this.conversationFactory = conversationFactory;
        this.messagingTemplate = messagingTemplate;
        this.configProperties = configProperties;
        this.handler = handler;
    }

    @GetMapping("/chatroom/createChatroom")
    public ResponseEntity<?> createChatroom (){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [createChatroom]", member.getName(), member.getId());

        String chatroomId = chatroomService.createChatroom(member);

        ResultStatus<String> resultStatus = new ResultStatus<>();

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(chatroomId);

        return ResponseEntity.ok(resultStatus);
    }


    @MessageMapping("/init")
    public void init (@RequestBody ChatInitDTO chatInitDTO) throws ExecutionException, InterruptedException, IOException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [chat init]", member.getName(), member.getId());

        final ConversationType conversationType = chatInitDTO.getConversationType();
        final String chatroomId = chatInitDTO.getChatroomId();
        final Scenario scenario = chatInitDTO.getScenario();
        final String lessonId = chatInitDTO.getLessonId();
        ChatRequestDTO chatRequestDTO = new ChatRequestDTO(
                chatroomId,
                scenario,
                member.getId(),
                member.getPartnerId(),
                lessonId
        );
        final AbstractConversation conversation = conversationFactory.createConversation(conversationType, chatRequestDTO);
        final ConversationChainDTO conversationChainDTO = conversation.startConversation();
        activeConversations.put(conversationChainDTO.getConversationId(), conversation);
        String chatroomDestination = "/chatroom/" + chatroomId + "/" + conversationType;
        messagingTemplate.convertAndSend(chatroomDestination, conversationChainDTO);
    }


    @PostMapping("/chatroom/upload")
    public ResponseEntity<ResultStatus<Map<String, String>>> uploadAudio (@ModelAttribute ChatDTO chatDTO) throws IOException, ExecutionException, InterruptedException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [upload]", member.getName(), member.getId());

        ResultStatus<Map<String, String>> resultStatus = new ResultStatus<>();

        // 儲存音檔 or 圖片
        final String fileName = handler.saveMultipartFile(chatDTO, configProperties);

        if (Files.size(Paths.get(fileName)) > 0) {  // 代表檔案儲存成功
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
            resultStatus.setData(Map.of(chatDTO.getMessageType().toString(), fileName));
        }

        return ResponseEntity.ok(resultStatus);
    }

    //TODO: 刪除檔案


    @MessageMapping("/chat")
    public void chat (@ModelAttribute ChatDTO chatDTO) throws ExecutionException, InterruptedException, IOException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [chat]", member.getName(), member.getId());

        final String chatroomId = chatDTO.getChatroomId();
        final ConversationType conversationType = chatDTO.getConversationType();

        ConversationChainDTO conversationChainDTO = chatroomService.reply(chatDTO, member);
        String chatroomDestination = "/chatroom/" + chatroomId + "/" + conversationType;
        messagingTemplate.convertAndSend(chatroomDestination, conversationChainDTO);
    }


    @PostMapping("/chatroom/end")
    public void end (@RequestBody ChatDTO chatDTO) throws IOException, ExecutionException, InterruptedException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [conversation end]", member.getName(), member.getId());

        ConversationChainDTO conversationChainDTO = chatroomService.end(chatDTO);

        if (StringUtils.isNotBlank(chatDTO.getLessonId()) && conversationChainDTO.getConversationScore() != null) {
            // 導向測驗結束結果頁面
        } else {
            // 導向首頁
        }
    }
}
