package com.gurula.talkyo.chatroom;

public class LearningReport {
    private ConversationScore conversationScore;
    private Feedback feedback;

    public LearningReport(ConversationScore conversationScore) {
        this.conversationScore = conversationScore;
    }

    public LearningReport(Feedback feedback) {
        this.feedback = feedback;
    }

    public LearningReport(ConversationScore conversationScore, Feedback feedback) {
        this.conversationScore = conversationScore;
        this.feedback = feedback;
    }

    public ConversationScore getConversationScore() {
        return conversationScore;
    }

    public Feedback getFeedback() {
        return feedback;
    }
}
