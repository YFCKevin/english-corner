package com.gurula.talkyo.chatroom;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
public class GrammarResult {
    private String errorSentence;
    private String correctSentence;
    private String translation;     // 正確語句的翻譯
    private String errorReason;

    public String getErrorSentence() {
        return errorSentence;
    }

    public void setErrorSentence(String errorSentence) {
        this.errorSentence = errorSentence;
    }

    public String getCorrectSentence() {
        return correctSentence;
    }

    public void setCorrectSentence(String correctSentence) {
        this.correctSentence = correctSentence;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }
}
