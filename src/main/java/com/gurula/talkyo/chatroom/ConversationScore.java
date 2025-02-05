package com.gurula.talkyo.chatroom;

import java.util.List;

public class ConversationScore {
    private double fluency; // 流暢度
    private double accuracy;    // 精准度
    private double completeness;    // 完整性
    private double prosody; // 韻律
    private String displayText;     // 識別文字
    private List<DisplayWord> displayWords;
    private ContentAssessment contentAssessment;    // 內容分析

    public ConversationScore() {
    }

    public ConversationScore(double fluency, double accuracy, double completeness, double prosody, String displayText, List<DisplayWord> displayWords) {
        this.fluency = fluency;
        this.accuracy = accuracy;
        this.completeness = completeness;
        this.prosody = prosody;
        this.displayText = displayText;
        this.displayWords = displayWords;
    }

    public ConversationScore(double fluency, double accuracy, double completeness, double prosody, ContentAssessment contentAssessment) {
        this.fluency = fluency;
        this.accuracy = accuracy;
        this.completeness = completeness;
        this.prosody = prosody;
        this.contentAssessment = contentAssessment;
    }

    public double getFluency() {
        return fluency;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getCompleteness() {
        return completeness;
    }

    public double getProsody() {
        return prosody;
    }

    public String getDisplayText() {
        return displayText;
    }

    public List<DisplayWord> getDisplayWords() {
        return displayWords;
    }

    public ContentAssessment getContentAssessment() {
        return contentAssessment;
    }

    public void setContentAssessment(ContentAssessment contentAssessment) {
        this.contentAssessment = contentAssessment;
    }
}
