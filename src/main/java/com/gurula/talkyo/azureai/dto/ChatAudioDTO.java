package com.gurula.talkyo.azureai.dto;

public class ChatAudioDTO {
    private final String content;
    private final String memberId;
    private final String partnerId;
    private final String conversationId;

    public ChatAudioDTO(String content, String memberId, String partnerId, String conversationId) {
        this.content = content;
        this.memberId = memberId;
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
