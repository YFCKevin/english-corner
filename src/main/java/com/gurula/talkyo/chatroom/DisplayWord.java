package com.gurula.talkyo.chatroom;

public class DisplayWord {
    private String word;
    private double accuracyScore;
    private String errorType;

    public DisplayWord() {
    }

    public DisplayWord(String word, double accuracyScore, String errorType) {
        this.word = word;
        this.accuracyScore = accuracyScore;
        this.errorType = errorType;
    }

    public double getAccuracyScore() {
        return accuracyScore;
    }

    public void setAccuracyScore(double accuracyScore) {
        this.accuracyScore = accuracyScore;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
