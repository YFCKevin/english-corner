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
            fileName = doSaveHandler(chatDTO, configProperties);
        } else if (next != null){
            fileName = next.saveMultipartFile(chatDTO, configProperties);
        }
        return fileName;
    }

    public void deleteFile (ChatDTO chatDTO, ConfigProperties configProperties) throws IOException {
        if (match(chatDTO.getMessageType())) {
            doDeleteHandler(chatDTO, configProperties);
        } else if (next != null){
            next.deleteFile(chatDTO, configProperties);
        }
    }

    public void deleteFiles (ChatDTO chatDTO, ConfigProperties configProperties) throws IOException {
        if (match(chatDTO.getMessageType())) {
            doBatchDeleteHandler(chatDTO, configProperties);
        } else if (next != null){
            next.deleteFiles(chatDTO, configProperties);
        }
    }

    protected abstract boolean match(MessageType messageType);
    protected abstract String doSaveHandler(ChatDTO chatDTO, ConfigProperties configProperties) throws IOException;
    protected abstract void doDeleteHandler(ChatDTO chatDTO, ConfigProperties configProperties) throws IOException;
    protected abstract void doBatchDeleteHandler(ChatDTO chatDTO, ConfigProperties configProperties) throws IOException;
}
