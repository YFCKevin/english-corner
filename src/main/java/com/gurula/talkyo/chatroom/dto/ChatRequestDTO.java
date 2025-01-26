package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.Scenario;

public class ChatRequestDTO {
    private String chatroomId;
    private Scenario scenario;
    private String conversationId;
    private String memberId;
    private String partnerId;
    private String lessonId;
    private String messageId;

    public ChatRequestDTO(String conversationId, String lessonId) {
        this.conversationId = conversationId;
        this.lessonId = lessonId;
    }

    public ChatRequestDTO(String memberId, String partnerId, String messageId, String lessonId) {
        this.memberId = memberId;
        this.partnerId = partnerId;
        this.messageId = messageId;
        this.lessonId = lessonId;
    }

    // for chat init
    public ChatRequestDTO(String chatroomId, Scenario scenario, String memberId, String partnerId, String lessonId) {
        this.chatroomId = chatroomId;
        this.scenario = scenario;
        this.memberId = memberId;
        this.partnerId = partnerId;
        this.lessonId = lessonId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }
}
