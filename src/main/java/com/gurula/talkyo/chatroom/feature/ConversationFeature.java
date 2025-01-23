package com.gurula.talkyo.chatroom.feature;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;

import java.util.concurrent.ExecutionException;

public interface ConversationFeature {
    ConversationChainDTO execute(AbstractConversation conversation, ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException;
}
