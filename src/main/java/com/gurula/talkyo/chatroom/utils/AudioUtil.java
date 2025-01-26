package com.gurula.talkyo.chatroom.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AudioUtil {

    public static void mergeAudioFiles(String outputFile, List<String> audioFilePaths) throws IOException {
        // 创建一个文件列表，用于FFmpeg合并音频
        StringBuilder fileList = new StringBuilder();
        for (String inputFile : audioFilePaths) {
            fileList.append("file '").append(inputFile).append("'\n");
        }

        // 将文件列表写入一个临时文本文件
        String tempFileName = "filelist.txt";
        final Path path = Paths.get(tempFileName);
        java.nio.file.Files.write(path, fileList.toString().getBytes());

        // FFmpeg命令来合并音频

        String command = "ffmpeg -f concat -safe 0 -i " + tempFileName + " -c copy " + outputFile;

        // 执行FFmpeg命令
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        Process process = processBuilder.start();

        // 等待命令执行完毕
        try {
            process.waitFor();
            System.out.println("Audio files merged successfully!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 删除临时的文件列表
            java.nio.file.Files.deleteIfExists(path);
        }
    }
}
