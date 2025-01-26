package com.gurula.talkyo.chatroom.factory;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.enums.ConversationType;
import com.gurula.talkyo.chatroom.feature.*;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

public class OneOnOneConversation extends AbstractConversation{
    public OneOnOneConversation(ChatRequestDTO chatRequestDTO, ApplicationContext applicationContext){
        super(ConversationType.ONE_ON_ONE, chatRequestDTO);

        setStartChain(List.of(
                applicationContext.getBean(SendWelcomeMessage.class)
        ));

        setChattingChain(Arrays.asList(
                applicationContext.getBean(SpeechToText.class),
                applicationContext.getBean(GrammarCheck.class),
                applicationContext.getBean(AIReplyMessage.class)
        ));
    }
}
