package com.gurula.talkyo.chatroom.factory;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.enums.ConversationType;
import com.gurula.talkyo.chatroom.feature.*;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

public class ImageConversation extends AbstractConversation{
    public ImageConversation(ChatRequestDTO chatRequestDTO, ApplicationContext applicationContext){
        super(ConversationType.IMAGE, chatRequestDTO);

        setStartChain(Arrays.asList(
                applicationContext.getBean(CreateScenarioConversation.class),
                applicationContext.getBean(SendWelcomeMessage.class)
        ));

        setChattingChain(Arrays.asList(
                applicationContext.getBean(SpeechToText.class),
                applicationContext.getBean(GrammarCheck.class),
                applicationContext.getBean(AIReplyMessage.class)
        ));

        setEndChain(List.of(
                applicationContext.getBean(MarkFinish.class)
        ));
    }
}
