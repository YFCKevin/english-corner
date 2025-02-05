package com.gurula.talkyo.chatroom.utils;

import com.gurula.talkyo.chatroom.enums.MessageType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileUtils {

    public static Path saveUploadedFile(MultipartFile file, MessageType messageType, Path uploadPath) throws IOException {

        String extension = "";
        if (messageType == MessageType.AUDIO) {
            extension = ".webm";
        } else if (messageType == MessageType.IMAGE) {
            extension = ".jpg";
        }

        // 確保目標資料夾存在
        Files.createDirectories(uploadPath);

        final long currentTime = System.currentTimeMillis();
        Path path = uploadPath.resolve(currentTime + extension);

        try (InputStream fileStream = file.getInputStream()) {
            Files.copy(fileStream, path);
        }

        if (messageType == MessageType.AUDIO) {
            Path wavPath = uploadPath.resolve(currentTime + ".wav");

            // 使用 FFmpeg 進行 webm 到 wav 的格式轉換
            convertWebmToWav(path, wavPath);

            // 刪除原始的 webm 檔案
            Files.delete(path);

            // 返回轉換後的 wav 檔案
            return wavPath;
        }

        return path;
    }

    private static void convertWebmToWav(Path webmFile, Path wavFile) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", webmFile.toString(), wavFile.toString());
        processBuilder.inheritIO();  // 可選，讓 FFmpeg 的輸出顯示在控制台
        Process process = processBuilder.start();

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg conversion failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFmpeg conversion was interrupted", e);
        }
    }
}
