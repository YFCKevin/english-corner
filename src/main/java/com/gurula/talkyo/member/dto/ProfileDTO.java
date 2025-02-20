package com.gurula.talkyo.member.dto;

public class ProfileDTO {
    private int completedCoursesCount;   // 已完成課程數
    private int numberOfSpeaking;     // 講話次數
    private String totalSpeakingDuration; // 講話時長
    private int currentWinStreak;        // 目前連勝
    private int bestWinStreak;           // 最佳連勝
    private int totalExp;               // 總經驗值

    public ProfileDTO() {
    }

    public ProfileDTO(int completedCoursesCount, int numberOfSpeaking, String totalSpeakingDuration, int totalExp, int currentWinStreak, int bestWinStreak) {
        this.completedCoursesCount = completedCoursesCount;
        this.numberOfSpeaking = numberOfSpeaking;
        this.totalSpeakingDuration = totalSpeakingDuration;
        this.totalExp = totalExp;
        this.currentWinStreak = currentWinStreak;
        this.bestWinStreak = bestWinStreak;
    }

    public int getCompletedCoursesCount() {
        return completedCoursesCount;
    }

    public int getNumberOfSpeaking() {
        return numberOfSpeaking;
    }

    public String getTotalSpeakingDuration() {
        return totalSpeakingDuration;
    }

    public int getCurrentWinStreak() {
        return currentWinStreak;
    }

    public int getBestWinStreak() {
        return bestWinStreak;
    }

    public int getTotalExp() {
        return totalExp;
    }
}
