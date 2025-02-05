package com.gurula.talkyo.chatroom.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AudioUtil {

    public static void mergeAudioFiles(String outputFile, List<String> audioFilePaths) throws IOException {

        Path outputPath = Paths.get(outputFile);
        Files.createDirectories(outputPath.getParent());

        StringBuilder fileList = new StringBuilder();
        for (String inputFile : audioFilePaths) {
            Path normalizedPath = Paths.get(inputFile).toAbsolutePath().normalize();
            fileList.append("file '").append(normalizedPath.toString().replace("\\", "/")).append("'\n");
        }


        String tempFileName = "filelist.txt";
        final Path path = Paths.get(tempFileName);
        java.nio.file.Files.write(path, fileList.toString().getBytes());

        String command = "ffmpeg -f concat -safe 0 -i " + tempFileName + " -c copy " + outputFile;

        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Audio files merged successfully!");
            } else {
                System.out.println("ffmpeg process failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
