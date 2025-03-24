package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.course.utils.CourseUtil;

import java.util.List;

public class AdvancedSentence {
    private String unitNumber;
    private String content;
    private String translation;
    private List<String> audioName;
    private String explanation;
    private boolean formal;

    public AdvancedSentence() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getAudioName() {
        return audioName;
    }

    public void setAudioName(List<String> audioName) {
        this.audioName = audioName;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isFormal() {
        return formal;
    }

    public void setFormal(boolean formal) {
        this.formal = formal;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public void genUnitNumber() {
        this.unitNumber = CourseUtil.genAdvancedSentenceUnitNumber();
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
