package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.Scenario;
import com.gurula.talkyo.chatroom.enums.ConversationType;

public class ChatInitDTO {
    private ConversationType conversationType;
    private String chatroomId;
    private Scenario scenario;
    private String lessonId;

    public ConversationType getConversationType() {
        return conversationType;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public String getLessonId() {
        return lessonId;
    }
}
