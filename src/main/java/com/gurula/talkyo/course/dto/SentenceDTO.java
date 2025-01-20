package com.gurula.talkyo.course.dto;

import com.gurula.talkyo.course.enums.SentenceLevel;

public class SentenceDTO {
    private String unitNumber;
    private String content;
    private SentenceLevel complexity;

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SentenceLevel getComplexity() {
        return complexity;
    }

    public void setComplexity(SentenceLevel complexity) {
        this.complexity = complexity;
    }
}
