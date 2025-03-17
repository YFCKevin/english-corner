package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.course.utils.CourseUtil;

public class GrammarResult {
    private String unitNumber;
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

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public void genUnitNumber() {
        this.unitNumber = CourseUtil.genGrammarResultUnitNumber();
    }
}
