package com.gurula.talkyo.chatroom.handler;

import com.gurula.talkyo.chatroom.dto.ChatDTO;
import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.properties.ConfigProperties;

import java.io.IOException;

public abstract class MessageTypeHandler {
    protected MessageTypeHandler next;

    public MessageTypeHandler(MessageTypeHandler next) {
        this.next = next;
    }

    public String saveMultipartFile (ChatDTO chatDTO, ConfigProperties configProperties) throws IOException {
        String fileName = "";
        if (match(chatDTO.getMessageType())) {
            fileName = doHandler(chatDTO, configProperties);
        } else if (next != null){
            fileName = doHandler(chatDTO, configProperties);
        }
        return fileName;
    }

    protected abstract boolean match(MessageType messageType);
    protected abstract String doHandler(ChatDTO chatDTO, ConfigProperties configProperties) throws IOException;
}
