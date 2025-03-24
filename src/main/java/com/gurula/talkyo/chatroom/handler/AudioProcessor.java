package com.gurula.talkyo.chatroom.handler;

import com.gurula.talkyo.chatroom.dto.ChatDTO;
import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.chatroom.utils.FileUtils;
import com.gurula.talkyo.properties.ConfigProperties;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AudioProcessor extends MessageTypeHandler{

    public AudioProcessor(MessageTypeHandler next) {
        super(next);
    }

    @Override
    protected boolean match(MessageType messageType) {
        return MessageType.AUDIO.equals(messageType);
    }

    @Override
    protected String doSaveHandler(ChatDTO chatDTO, ConfigProperties configProperties) throws IOException {
        Path uploadPath = Paths.get(configProperties.getAudioSavePath(), chatDTO.getChatroomId());
        Path path = FileUtils.saveUploadedFile(chatDTO.getMultipartFile(), MessageType.AUDIO, uploadPath);
        return path.getFileName().toString();
    }

    @Override
    protected void doDeleteHandler(ChatDTO chatDTO, ConfigProperties configProperties) throws IOException {
        final String audioFileName = chatDTO.getAudioFileName();
        Path filePath = Paths.get(configProperties.getAudioSavePath(), chatDTO.getChatroomId(), audioFileName);
        FileUtils.deleteFile(filePath);
    }

    @Override
    protected void doBatchDeleteHandler(ChatDTO chatDTO, ConfigProperties configProperties) throws IOException {
        List<Path> filePaths = new ArrayList<>();
        final List<String> audioFileNames = chatDTO.getAudioFileNames();
        for (String audioFileName : audioFileNames) {
            Path filePath = Paths.get(configProperties.getAudioSavePath(), chatDTO.getChatroomId(), audioFileName);
            filePaths.add(filePath);
        }
        FileUtils.deleteFiles(filePaths);
    }
}
