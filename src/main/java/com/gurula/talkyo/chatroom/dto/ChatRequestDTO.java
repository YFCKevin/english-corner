package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.Scenario;
import com.gurula.talkyo.chatroom.enums.ChatroomType;

public class ChatRequestDTO {
    private String chatroomId;
    private Scenario scenario;
    private String memberId;
    private String partnerId;
    private String lessonId;
    private String messageId;
    private ChatroomType chatroomType;

    // for pronunciation
    private String referenceText;
    private String audioFilePath;

    // for free talk
    private String branch;
    private String previewMessageId;

    public ChatRequestDTO() {
    }

    public ChatRequestDTO(String chatroomId, ChatroomType chatroomType, Scenario scenario) {
        this.chatroomId = chatroomId;
        this.chatroomType = chatroomType;
        this.scenario = scenario;
    }

    public ChatRequestDTO(String messageId) {
        this.messageId = messageId;
    }

    public ChatRequestDTO(String messageId, String referenceText, String audioFilePath, String partnerId) {
        this.messageId = messageId;
        this.referenceText = referenceText;
        this.audioFilePath = audioFilePath;
        this.partnerId = partnerId;
    }

    public ChatRequestDTO(ChatroomType chatroomType, String messageId, String branch, String previewMessageId, String partnerId) {
        this.chatroomType = chatroomType;
        this.messageId = messageId;
        this.branch = branch;
        this.previewMessageId = previewMessageId;
        this.partnerId = partnerId;
    }

    public ChatRequestDTO(String chatroomId, ChatroomType chatroomType, String branch, String previewMessageId) {
        this.chatroomId = chatroomId;
        this.chatroomType = chatroomType;
        this.branch = branch;
        this.previewMessageId = previewMessageId;
    }

    public ChatRequestDTO(String chatroomId, String memberId, String partnerId, String messageId, String lessonId, ChatroomType chatroomType, String branch, String previewMessageId) {
        this.chatroomId = chatroomId;
        this.memberId = memberId;
        this.partnerId = partnerId;
        this.messageId = messageId;
        this.lessonId = lessonId;
        this.chatroomType = chatroomType;
        this.branch = branch;
        this.previewMessageId = previewMessageId;
    }

    // for chat init
    public ChatRequestDTO(String chatroomId, Scenario scenario, String memberId, String partnerId, String lessonId, ChatroomType chatroomType) {
        this.chatroomId = chatroomId;
        this.scenario = scenario;
        this.memberId = memberId;
        this.partnerId = partnerId;
        this.lessonId = lessonId;
        this.chatroomType = chatroomType;
    }

    public ChatRequestDTO(String chatroomId, String partnerId, ChatroomType chatroomType) {
        this.chatroomId = chatroomId;
        this.partnerId = partnerId;
        this.chatroomType = chatroomType;
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

    public ChatroomType getChatroomType() {
        return chatroomType;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setChatroomType(ChatroomType chatroomType) {
        this.chatroomType = chatroomType;
    }

    public String getReferenceText() {
        return referenceText;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setReferenceText(String referenceText) {
        this.referenceText = referenceText;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public String getBranch() {
        return branch;
    }

    public String getPreviewMessageId() {
        return previewMessageId;
    }

    @Override
    public String toString() {
        return "ChatRequestDTO{" +
                "chatroomId='" + chatroomId + '\'' +
                ", scenario=" + scenario +
                ", memberId='" + memberId + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", lessonId='" + lessonId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", chatroomType=" + chatroomType +
                ", referenceText='" + referenceText + '\'' +
                ", audioFilePath='" + audioFilePath + '\'' +
                ", branch='" + branch + '\'' +
                ", previewMessageId='" + previewMessageId + '\'' +
                '}';
    }
}
