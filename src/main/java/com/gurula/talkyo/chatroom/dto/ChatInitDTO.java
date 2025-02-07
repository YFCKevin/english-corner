package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.Scenario;
import com.gurula.talkyo.chatroom.enums.ChatroomType;

public class ChatInitDTO {
    private ChatroomType chatroomType;
    private String chatroomId;
    private Scenario scenario;
    private String lessonId;
    private String courseId;
    private String currentMessageId;    // 當前訊息的編號 (儲存在 localStorage)

    public ChatroomType getChatroomType() {
        return chatroomType;
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

    public String getCourseId() {
        return courseId;
    }

    public String getCurrentMessageId() {
        return currentMessageId;
    }

    @Override
    public String toString() {
        return "ChatInitDTO{" +
                "chatroomType=" + chatroomType +
                ", chatroomId='" + chatroomId + '\'' +
                ", scenario=" + scenario +
                ", lessonId='" + lessonId + '\'' +
                '}';
    }
}
