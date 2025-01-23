package com.gurula.talkyo.chatroom;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "conversation_score")
public class ConversationScore {
    @Id
    private String id;
    private double fluency; // 流暢度
    private double accuracy;    // 精准度
    private double completeness;    // 完整性
    private double prosody; // 韻律
    private ContentAssessment contentAssessment;    // 內容分析

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
