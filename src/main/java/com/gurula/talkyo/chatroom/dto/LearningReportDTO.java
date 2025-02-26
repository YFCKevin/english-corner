package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.ConversationScore;
import com.gurula.talkyo.chatroom.Feedback;

public class LearningReportDTO {
    private String title;
    private ConversationScore conversationScore;
    private Feedback feedback;
    private double overallRating;   // 綜合評分 (fluency, accuracy, completeness, prosody 的平均)

    public LearningReportDTO() {
    }

    public LearningReportDTO(String title, ConversationScore conversationScore, Feedback feedback, double overallRating) {
        this.title = title;
        this.conversationScore = conversationScore;
        this.feedback = feedback;
        this.overallRating = overallRating;
    }

    public String getTitle() {
        return title;
    }

    public ConversationScore getConversationScore() {
        return conversationScore;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public double getOverallRating() {
        return overallRating;
    }
}
