package com.gurula.talkyo.chatroom.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface ConversationFeature {
    <T extends AbstractConversation> ConversationChainDTO execute(T conversation, ChatRequestDTO chatRequestDTO) throws ExecutionException, InterruptedException, IOException;
}
