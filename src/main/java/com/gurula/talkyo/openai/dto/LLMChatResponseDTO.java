package com.gurula.talkyo.openai.dto;

public class LLMChatResponseDTO {
    private String content;
    private String translation;

    public LLMChatResponseDTO() {
    }

    public LLMChatResponseDTO(String content, String translation) {
        this.content = content;
        this.translation = translation;
    }

    public String getContent() {
        return content;
    }

    public String getTranslation() {
        return translation;
    }
}
