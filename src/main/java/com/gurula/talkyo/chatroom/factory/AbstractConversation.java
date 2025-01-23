package com.gurula.talkyo.chatroom.factory;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.enums.ConversationType;
import com.gurula.talkyo.chatroom.feature.ConversationFeature;

import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class AbstractConversation {
    private ConversationType conversationType;
    private List<ConversationFeature> startChain;
    private List<ConversationFeature> endChain;
    private List<ConversationFeature> chattingChain;
    private ChatRequestDTO chatRequestDTO;

    public AbstractConversation (ConversationType conversationType, ChatRequestDTO chatRequestDTO){
        this.conversationType = conversationType;
        this.chatRequestDTO = chatRequestDTO;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public List<ConversationFeature> getStartChain() {
        return startChain;
    }

    public void setStartChain(List<ConversationFeature> startChain) {
        this.startChain = startChain;
    }

    public List<ConversationFeature> getEndChain() {
        return endChain;
    }

    public void setEndChain(List<ConversationFeature> endChain) {
        this.endChain = endChain;
    }

    public List<ConversationFeature> getChattingChain() {
        return chattingChain;
    }

    public void setChattingChain(List<ConversationFeature> chattingChain) {
        this.chattingChain = chattingChain;
    }

    public ConversationChainDTO startConversation() throws ExecutionException, InterruptedException {

        ConversationChainDTO finalResult = new ConversationChainDTO();

        for (ConversationFeature feature : startChain) {
            final ConversationChainDTO result = feature.execute(this, chatRequestDTO);

            if (result.getConversationId() != null) {
                System.out.println("Created conversation with ID: " + result.getConversationId());
                finalResult.setConversationId(result.getConversationId());
            }
            if (result.getMessage() != null) {
                System.out.println("Sent message: " + result.getMessage().getContent());
                finalResult.setMessage(result.getMessage());
            }
        }

        return finalResult;
    }

    public void finish() throws ExecutionException, InterruptedException {
        for (ConversationFeature feature : endChain) {
            final ConversationChainDTO result = feature.execute(this, chatRequestDTO);

        }
    }

    public void chatting() throws ExecutionException, InterruptedException {
        for (ConversationFeature feature : chattingChain) {
            final ConversationChainDTO result = feature.execute(this, chatRequestDTO);

        }
    }
}
