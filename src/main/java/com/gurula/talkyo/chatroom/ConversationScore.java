package com.gurula.talkyo.chatroom;

public class ConversationScore {
    private double fluency; // 流暢度
    private double accuracy;    // 精准度
    private double completeness;    // 完整性
    private double prosody; // 韻律
    private ContentAssessment contentAssessment;    // 內容分析

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

    public void setFluency(double fluency) {
        this.fluency = fluency;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getCompleteness() {
        return completeness;
    }

    public void setCompleteness(double completeness) {
        this.completeness = completeness;
    }

    public double getProsody() {
        return prosody;
    }

    public void setProsody(double prosody) {
        this.prosody = prosody;
    }

    public ContentAssessment getContentAssessment() {
        return contentAssessment;
    }

    public void setContentAssessment(ContentAssessment contentAssessment) {
        this.contentAssessment = contentAssessment;
    }
}
