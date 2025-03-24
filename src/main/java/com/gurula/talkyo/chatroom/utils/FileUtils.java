package com.gurula.talkyo.chatroom.utils;

import com.gurula.talkyo.chatroom.enums.MessageType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

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
            Files.copy(fileStream, path, StandardCopyOption.REPLACE_EXISTING);  // 防止 FileAlreadyExistsException
        }

        if (messageType == MessageType.AUDIO) {
            Path wavPath = uploadPath.resolve(currentTime + ".wav");

            // 使用 FFmpeg 進行 webm 到 wav 的格式轉換
            convertWebmToWav(path, wavPath);

            // 刪除原始的 webm 檔案
            Files.deleteIfExists(path); // 防止 NoSuchFileException

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


    public static void deleteFile(Path filePath) throws IOException {
        // 檢查檔案是否存在
        if (Files.exists(filePath)) {
            try {
                // 刪除檔案
                Files.delete(filePath);
                System.out.println("File deleted successfully: " + filePath);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + filePath);
                throw e;
            }
        } else {
            System.out.println("File does not exist: " + filePath);
        }
    }


    public static void deleteFiles(List<Path> filePaths) {
        for (Path filePath : filePaths) {
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("File deleted successfully: " + filePath);
                } else {
                    System.out.println("File does not exist: " + filePath);
                }
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + filePath + " - " + e.getMessage());
            }
        }
    }
}
