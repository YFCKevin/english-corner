package com.gurula.talkyo.course.utils;

import java.util.Random;

public class CourseUtil {

    public static String genLessonNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("LS");

        // 產生隨機的4個數字
        for (int i = 0; i < 4; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }

    public static String genSentenceUnitNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("ST");

        // 產生隨機的5個數字
        for (int i = 0; i < 5; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }
}
