package com.gurula.talkyo.openai.dto;

public class GrammarResponseDTO {
    private String correctSentence;
    private String translation;
    private String errorReason;

    public GrammarResponseDTO() {
    }

    public GrammarResponseDTO(String correctSentence, String translation, String errorReason) {
        this.correctSentence = correctSentence;
        this.translation = translation;
        this.errorReason = errorReason;
    }

    public String getCorrectSentence() {
        return correctSentence;
    }

    public String getTranslation() {
        return translation;
    }

    public String getErrorReason() {
        return errorReason;
    }

    @Override
    public String toString() {
        return "GrammarResponseDTO{" +
                "correctSentence='" + correctSentence + '\'' +
                ", translation='" + translation + '\'' +
                ", errorReason='" + errorReason + '\'' +
                '}';
    }
}
