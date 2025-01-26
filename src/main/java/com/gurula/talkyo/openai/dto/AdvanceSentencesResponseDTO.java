package com.gurula.talkyo.openai.dto;

public class AdvanceSentencesResponseDTO {
    private boolean formal;
    private String explanation;
    private String sentence;

    public boolean isFormal() {
        return formal;
    }

    public void setFormal(boolean formal) {
        this.formal = formal;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    @Override
    public String toString() {
        return "AdvanceSentencesResponseDTO{" +
                "formal=" + formal +
                ", explanation='" + explanation + '\'' +
                ", sentence='" + sentence + '\'' +
                '}';
    }
}
