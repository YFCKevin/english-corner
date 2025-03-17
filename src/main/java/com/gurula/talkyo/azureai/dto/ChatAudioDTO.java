package com.gurula.talkyo.azureai.dto;

public class ChatAudioDTO {
    private String content;
    private String memberId;
    private String partnerId;
    private String chatroomId;
    private String unitNumber;

    public ChatAudioDTO() {
    }

    public ChatAudioDTO(String content, String memberId, String partnerId, String chatroomId) {
        this.content = content;
        this.memberId = memberId;
        this.partnerId = partnerId;
        this.chatroomId = chatroomId;
    }

    public ChatAudioDTO(String content, String partnerId, String chatroomId) {
        this.content = content;
        this.partnerId = partnerId;
        this.chatroomId = chatroomId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getContent() {
        return content;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    @Override
    public String toString() {
        return "ChatAudioDTO{" +
                "content='" + content + '\'' +
                ", memberId='" + memberId + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", chatroomId='" + chatroomId + '\'' +
                '}';
    }
}
