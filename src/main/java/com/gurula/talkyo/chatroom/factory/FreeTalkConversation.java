package com.gurula.talkyo.chatroom.factory;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.enums.ConversationType;
import com.gurula.talkyo.chatroom.feature.*;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

public class FreeTalkConversation extends AbstractConversation{
    public FreeTalkConversation(ChatRequestDTO chatRequestDTO, ApplicationContext applicationContext) {
        super(ConversationType.FREE_TALK, chatRequestDTO);

        setStartChain(Arrays.asList(
                applicationContext.getBean(CreateScenarioConversation.class),
                applicationContext.getBean(SendWelcomeMessage.class)
        ));

        setChattingChain(List.of(
                applicationContext.getBean(SaveAudioFile.class)
        ));
    }
}