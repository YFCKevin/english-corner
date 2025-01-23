package com.gurula.talkyo.chatroom.factory;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.enums.ConversationType;
import com.gurula.talkyo.chatroom.feature.*;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
public class LearningPlanConversation extends AbstractConversation {

    public LearningPlanConversation(ChatRequestDTO chatRequestDTO, ApplicationContext applicationContext) {
        super(ConversationType.PROJECT, chatRequestDTO);

        setStartChain(Arrays.asList(
                applicationContext.getBean(CreateScenarioConversation.class),
                applicationContext.getBean(SendWelcomeMessage.class)
        ));

        setChattingChain(Arrays.asList(
                applicationContext.getBean(GrammarCheck.class),
                applicationContext.getBean(GenAdvanceSentences.class),
                applicationContext.getBean(SaveAudioFile.class),
                applicationContext.getBean(AIReplyMessage.class)
        ));

        setEndChain(Arrays.asList(
                applicationContext.getBean(MarkFinish.class),
                applicationContext.getBean(GenLearningReport.class)
        ));
    }
}