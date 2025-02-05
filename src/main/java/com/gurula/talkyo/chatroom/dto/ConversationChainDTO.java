package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.ConversationScore;
import com.gurula.talkyo.chatroom.Message;

import java.util.ArrayList;
import java.util.List;

public class ConversationChainDTO {

    // start chain
    private String conversationId;

    // chatting chain
    private List<Message> messages = new ArrayList<>();

    // end chain
    private ConversationScore conversationScore;

    public ConversationChainDTO() {
    }

    public ConversationChainDTO(String conversationId) {
        this.conversationId = conversationId;
    }

    public ConversationChainDTO(List<Message> messages) {
        this.messages = messages;
    }

    public ConversationChainDTO(String conversationId, List<Message> messages, ConversationScore conversationScore) {
        this.conversationId = conversationId;
        this.messages = messages;
        this.conversationScore = conversationScore;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public ConversationScore getConversationScore() {
        return conversationScore;
    }

    public void setConversationScore(ConversationScore conversationScore) {
        this.conversationScore = conversationScore;
    }
}
