package com.gurula.talkyo.chatroom.feature;

import com.gurula.talkyo.chatroom.Conversation;
import com.gurula.talkyo.chatroom.ConversationRepository;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Component
public class MarkFinish implements ConversationFeature{

    private final ConversationRepository conversationRepository;
    private final SimpleDateFormat sdf;

    public MarkFinish(ConversationRepository conversationRepository, @Qualifier("sdf") SimpleDateFormat sdf) {
        this.conversationRepository = conversationRepository;
        this.sdf = sdf;
    }

    @Override
    public <T extends AbstractConversation> ConversationChainDTO execute(T conversation, ChatRequestDTO chatRequestDTO) {
        System.out.println("MarkFinish");

        final String conversationId = chatRequestDTO.getConversationId();
        final Optional<Conversation> opt = conversationRepository.findById(conversationId);
        if (opt.isPresent()) {
            final Conversation conversationInDB = opt.get();
            conversationInDB.setFinishedDateTime(sdf.format(new Date()));
            conversationRepository.save(conversationInDB);
        }

        return null;
    }
}
