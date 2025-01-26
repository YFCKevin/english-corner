package com.gurula.talkyo.chatroom.feature;

import com.gurula.talkyo.chatroom.Conversation;
import com.gurula.talkyo.chatroom.ConversationRepository;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import com.gurula.talkyo.chatroom.factory.LearningPlanConversation;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CreateScenarioConversation implements ConversationFeature {
    private final ConversationRepository conversationRepository;
    private final SimpleDateFormat sdf;

    public CreateScenarioConversation(ConversationRepository conversationRepository, SimpleDateFormat sdf) {
        this.conversationRepository = conversationRepository;
        this.sdf = sdf;
    }


    @Override
    public <T extends AbstractConversation> ConversationChainDTO execute(T conversation, ChatRequestDTO chatRequestDTO) {
        System.out.println("CreateScenarioConversation");
        Conversation c = new Conversation();
        c.setChatroomId(chatRequestDTO.getChatroomId());
        c.setScenario(chatRequestDTO.getScenario());
        c.setStartedDateTime(sdf.format(new Date()));
        c.setConversationType(conversation.getConversationType());
        final Conversation savedConversation = conversationRepository.save(c);
        return new ConversationChainDTO(savedConversation.getId());
    }
}
