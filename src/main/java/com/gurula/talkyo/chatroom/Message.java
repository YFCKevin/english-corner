package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.SenderRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "message")
public class Message {
    @Id
    private String id;
    private String content;
    private String audioPath;
    private String conversationId;
    private String sender;  // memberId or partnerId
    private SenderRole senderRole;
    private String createdDateTime;
    private String updatedDateTime;

    // for AI
    private String translation;

    // for Human
    private boolean accuracy;
    private GrammarResult grammarResult;
    private ConversationScore conversationScore;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public SenderRole getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(SenderRole senderRole) {
        this.senderRole = senderRole;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public boolean isAccuracy() {
        return accuracy;
    }

    public void setAccuracy(boolean accuracy) {
        this.accuracy = accuracy;
    }

    public GrammarResult getGrammarResult() {
        return grammarResult;
    }

    public void setGrammarResult(GrammarResult grammarResult) {
        this.grammarResult = grammarResult;
    }

    public ConversationScore getConversationScore() {
        return conversationScore;
    }

    public void setConversationScore(ConversationScore conversationScore) {
        this.conversationScore = conversationScore;
    }
}
