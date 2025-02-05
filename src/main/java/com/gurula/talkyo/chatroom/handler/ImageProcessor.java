package com.gurula.talkyo.chatroom.handler;

import com.gurula.talkyo.chatroom.dto.ChatDTO;
import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.chatroom.utils.FileUtils;
import com.gurula.talkyo.properties.ConfigProperties;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageProcessor extends MessageTypeHandler{
    public ImageProcessor(MessageTypeHandler next) {
        super(next);
    }

    @Override
    protected boolean match(MessageType messageType) {
        return MessageType.IMAGE.equals(messageType);
    }

    @Override
    protected String doHandler(ChatDTO chatDTO, ConfigProperties configProperties) throws IOException {
        Path uploadPath = Paths.get(configProperties.getPicSavePath(), chatDTO.getChatroomId());
        Path path = FileUtils.saveUploadedFile(chatDTO.getMultipartFile(), MessageType.IMAGE, uploadPath);
        return path.getFileName().toString();
    }
}
