package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.dto.ChatInitDTO;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.enums.ConversationType;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import com.gurula.talkyo.chatroom.factory.ConversationFactory;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class ChatroomController {
    private final Logger logger = LoggerFactory.getLogger(ChatroomController.class);
    private final ChatroomService chatroomService;
    private final ConversationFactory conversationFactory;
    private final SimpMessageSendingOperations messagingTemplate;

    public ChatroomController(ChatroomService chatroomService, ConversationFactory conversationFactory, SimpMessageSendingOperations messagingTemplate) {
        this.chatroomService = chatroomService;
        this.conversationFactory = conversationFactory;
        this.messagingTemplate = messagingTemplate;
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
    public void init (@RequestBody ChatInitDTO chatInitDTO) throws ExecutionException, InterruptedException {
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
        String chatroomDestination = "/chatroom/" + chatroomId + "/" + conversationType;
        messagingTemplate.convertAndSend(chatroomDestination, conversationChainDTO.getMessage());
    }
}
