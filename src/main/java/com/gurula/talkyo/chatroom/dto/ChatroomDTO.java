package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.enums.ActionType;
import com.gurula.talkyo.chatroom.enums.ChatroomType;

public class ChatroomDTO {
    private ChatroomType chatroomType;
    private ActionType action;  // 用在自由談話聊天室中，使用者選擇創立新的 or 選擇舊的
    private String lessonId;    // For PROJECT

    public ActionType getAction() {
        return action;
    }

    public ChatroomType getChatroomType() {
        return chatroomType;
    }

    public String getLessonId() {
        return lessonId;
    }
}
