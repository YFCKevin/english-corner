package com.gurula.talkyo.chatroom.feature;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import org.springframework.stereotype.Component;

@Component
public class SaveAudioFile implements ConversationFeature{
    @Override
    public ConversationChainDTO execute(AbstractConversation conversation, ChatRequestDTO chatRequestDTO) {
        return null;
    }
}
