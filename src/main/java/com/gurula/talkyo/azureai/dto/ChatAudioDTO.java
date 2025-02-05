package com.gurula.talkyo.azureai.dto;

public class ChatAudioDTO {
    private String content;
    private String memberId;
    private String partnerId;
    private String chatroomId;

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
