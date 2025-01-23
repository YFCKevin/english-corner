package com.gurula.talkyo.chatroom.factory;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.enums.ConversationType;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ConversationFactory {
    private final ApplicationContext applicationContext;

    public ConversationFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public AbstractConversation createConversation(ConversationType conversationType, ChatRequestDTO chatRequestDTO) {
        return switch (conversationType) {
            case PROJECT -> new LearningPlanConversation(chatRequestDTO, applicationContext);
            case IMAGE -> new ImageConversation(chatRequestDTO, applicationContext);
            case SITUATION -> new SituationConversation(chatRequestDTO, applicationContext);
            case ONE_ON_ONE -> new OneOnOneConversation(chatRequestDTO, applicationContext);
            case FREE_TALK -> new FreeTalkConversation(chatRequestDTO, applicationContext);
        };
    }
}
