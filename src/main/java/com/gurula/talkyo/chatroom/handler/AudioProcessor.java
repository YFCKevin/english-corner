package com.gurula.talkyo.chatroom.handler;

import com.gurula.talkyo.chatroom.dto.ChatDTO;
import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.chatroom.utils.FileUtils;
import com.gurula.talkyo.properties.ConfigProperties;

import java.io.IOException;
import java.nio.file.Path;

public class AudioProcessor extends MessageTypeHandler{

    public AudioProcessor(MessageTypeHandler next) {
        super(next);
    }

    @Override
    protected boolean match(MessageType messageType) {
        return MessageType.AUDIO.equals(messageType);
    }

    @Override
    protected String doHandler(ChatDTO chatDTO, ConfigProperties configProperties) throws IOException {
        Path path = FileUtils.saveUploadedFile(chatDTO.getMultipartFile(), configProperties.getAudioSavePath());
        return path.getFileName().toString();
    }
}
