package com.gurula.talkyo.chatroom;

public class ContentAssessment {
    private double vocabularyScore; // 詞彙分數
    private double grammarScore;    // 文法分數
    private double topicScore;  // 主題分數

    public ContentAssessment(double vocabularyScore, double grammarScore, double topicScore) {
        this.vocabularyScore = vocabularyScore;
        this.grammarScore = grammarScore;
        this.topicScore = topicScore;
    }

    public double getVocabularyScore() {
        return vocabularyScore;
    }

    public double getGrammarScore() {
        return grammarScore;
    }

    public double getTopicScore() {
        return topicScore;
    }
}
