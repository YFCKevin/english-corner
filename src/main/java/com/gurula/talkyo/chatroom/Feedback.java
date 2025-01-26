package com.gurula.talkyo.chatroom;

public class Feedback {
    private String comment;
    private String translation;

    public Feedback(String comment, String translation) {
        this.comment = comment;
        this.translation = translation;
    }

    public String getComment() {
        return comment;
    }

    public String getTranslation() {
        return translation;
    }
}
