package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.enums.MessageType;

public class FileRequestDTO {
    private String fileName;
    private MessageType messageType;
    private String chatroomId;

    public MessageType getMessageType() {
        return messageType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getChatroomId() {
        return chatroomId;
    }
}
