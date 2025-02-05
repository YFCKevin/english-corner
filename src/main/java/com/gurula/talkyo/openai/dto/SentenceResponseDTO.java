package com.gurula.talkyo.openai.dto;

public class SentenceResponseDTO {
    private String unitNumber;
    private String content;
    private String translation;

    public String getUnitNumber() {
        return unitNumber;
    }

    public String getContent() {
        return content;
    }

    public String getTranslation() {
        return translation;
    }

    @Override
    public String toString() {
        return "SentenceResponseDTO{" +
                "unitNumber='" + unitNumber + '\'' +
                ", content='" + content + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }
}
