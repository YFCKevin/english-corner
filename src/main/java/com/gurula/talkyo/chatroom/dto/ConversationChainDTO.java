package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.ConversationScore;
import com.gurula.talkyo.chatroom.Message;

public class ConversationChainDTO {

    // start chain
    private String conversationId;

    // chatting chain
    private Message message;

    // end chain
    private ConversationScore conversationScore;

    public ConversationChainDTO() {
    }

    public ConversationChainDTO(String conversationId) {
        this.conversationId = conversationId;
    }

    public ConversationChainDTO(String conversationId, Message message, ConversationScore conversationScore) {
        this.conversationId = conversationId;
        this.message = message;
        this.conversationScore = conversationScore;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public ConversationScore getConversationScore() {
        return conversationScore;
    }

    public void setConversationScore(ConversationScore conversationScore) {
        this.conversationScore = conversationScore;
    }
}
