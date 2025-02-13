package com.gurula.talkyo.gemini.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class GeminiUtil {

    public static String encodeImageToBase64(String imagePath) {
        try {
            File file = new File(imagePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            fis.close();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
