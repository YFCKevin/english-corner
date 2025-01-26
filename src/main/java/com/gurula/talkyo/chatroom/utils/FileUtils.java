package com.gurula.talkyo.chatroom.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileUtils {

    public static Path saveUploadedFile(MultipartFile file, String uploadPath) throws IOException {

        Path path = Paths.get(uploadPath, String.valueOf(System.currentTimeMillis()));

        try (InputStream fileStream = file.getInputStream()) {
            Files.copy(fileStream, path);
        }

        return path;
    }
}
