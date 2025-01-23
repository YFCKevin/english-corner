package com.gurula.talkyo.chatroom;

public class ContentAssessment {
    private String vocabularyScore; // 詞彙分數
    private String grammarScore;    // 文法分數
    private String topicScore;  // 主題分數

    public String getVocabularyScore() {
        return vocabularyScore;
    }

    public void setVocabularyScore(String vocabularyScore) {
        this.vocabularyScore = vocabularyScore;
    }

    public String getGrammarScore() {
        return grammarScore;
    }

    public void setGrammarScore(String grammarScore) {
        this.grammarScore = grammarScore;
    }

    public String getTopicScore() {
        return topicScore;
    }

    public void setTopicScore(String topicScore) {
        this.topicScore = topicScore;
    }
}
