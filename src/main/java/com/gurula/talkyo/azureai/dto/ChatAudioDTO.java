package com.gurula.talkyo.azureai.dto;

public class ChatAudioDTO {
    private String content;
    private String memberId;
    private String partnerId;
    private String conversationId;

    public ChatAudioDTO(String content, String memberId, String partnerId, String conversationId) {
        this.content = content;
        this.memberId = memberId;
        this.partnerId = partnerId;
        this.conversationId = conversationId;
    }

    public ChatAudioDTO(String content, String partnerId, String conversationId) {
        this.content = content;
        this.partnerId = partnerId;
        this.conversationId = conversationId;
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

    public String getConversationId() {
        return conversationId;
    }
}
