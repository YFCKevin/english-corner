package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.ConversationScore;
import com.gurula.talkyo.chatroom.Feedback;

public class LearningReportDTO {
    private String lessonName;
    private ConversationScore conversationScore;
    private Feedback feedback;

    public LearningReportDTO() {
    }

    public LearningReportDTO(String lessonName, ConversationScore conversationScore, Feedback feedback) {
        this.lessonName = lessonName;
        this.conversationScore = conversationScore;
        this.feedback = feedback;
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
}
