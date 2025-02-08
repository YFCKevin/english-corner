package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.ConversationScore;
import com.gurula.talkyo.chatroom.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConversationChainDTO {
    private boolean humanMsg;

    // end chain
    private List<Map<Integer, Message>> messages = new ArrayList<>();

    private ConversationScore conversationScore;

    public ConversationChainDTO() {
    }

    public ConversationChainDTO(boolean humanMsg, List<Map<Integer, Message>> messages) {
        this.humanMsg = humanMsg;
        this.messages = messages;
    }

    public ConversationChainDTO(List<Map<Integer, Message>> messages, ConversationScore conversationScore) {
        this.messages = messages;
        this.conversationScore = conversationScore;
    }

    public List<Map<Integer, Message>> getMessages() {
        return messages;
    }

    public ConversationScore getConversationScore() {
        return conversationScore;
    }

    public void setConversationScore(ConversationScore conversationScore) {
        this.conversationScore = conversationScore;
    }
}
