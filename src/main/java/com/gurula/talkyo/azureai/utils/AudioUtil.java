package com.gurula.talkyo.azureai.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioUtil {

    public static double getAudioDurationInSeconds(String audioFilePath) throws UnsupportedAudioFileException, IOException {
        File audioFile = new File(audioFilePath);
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            double frameRate = format.getFrameRate();
            return (frames / frameRate);
        }
    }


    public static double getTotalAudioDuration(List<String> audioFilePaths) {
        double totalDuration = 0.0;
        for (String audioFilePath : audioFilePaths) {
            try {
                double duration = getAudioDurationInSeconds(audioFilePath);
                totalDuration += duration;
            } catch (UnsupportedAudioFileException e) {
                System.err.println("Unsupported audio file format: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error reading audio file: " + e.getMessage());
            }
        }
        return totalDuration;
    }
}
