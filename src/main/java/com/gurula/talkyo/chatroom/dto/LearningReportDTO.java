package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.ConversationScore;
import com.gurula.talkyo.chatroom.Feedback;

public class LearningReportDTO {
    private String lessonName;
    private ConversationScore conversationScore;
    private Feedback feedback;
    private double overallRating;   // 綜合評分 (fluency, accuracy, completeness, prosody 的平均)

    public LearningReportDTO() {
    }

    public LearningReportDTO(String lessonName, ConversationScore conversationScore, Feedback feedback, double overallRating) {
        this.lessonName = lessonName;
        this.conversationScore = conversationScore;
        this.feedback = feedback;
        this.overallRating = overallRating;
    }

    public String getLessonName() {
        return lessonName;
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
