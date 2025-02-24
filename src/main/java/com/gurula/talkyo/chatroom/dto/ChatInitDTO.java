package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.Scenario;
import com.gurula.talkyo.chatroom.enums.ChatroomType;

public class ChatInitDTO {
    private ChatroomType chatroomType;
    private String chatroomId;
    private String lessonId;
    private String courseId;
    private String currentMessageId;    // 當前訊息的編號 (儲存在 localStorage)
    private String unitNumber;

    public ChatroomType getChatroomType() {
        return chatroomType;
    }

    public String getChatroomId() {
        return chatroomId;
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

    public String getUnitNumber() {
        return unitNumber;
    }

    @Override
    public String toString() {
        return "ChatInitDTO{" +
                "chatroomType=" + chatroomType +
                ", chatroomId='" + chatroomId + '\'' +
                ", lessonId='" + lessonId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", currentMessageId='" + currentMessageId + '\'' +
                ", unitNumber='" + unitNumber + '\'' +
                '}';
    }
}
