package com.gurula.talkyo.course;

import com.gurula.talkyo.course.enums.SentenceLevel;
import com.gurula.talkyo.course.utils.CourseUtil;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

public class Sentence {
    private String unitNumber;
    private String content;
    private String translation;
    private List<String> audioName;
    private SentenceLevel complexity;
    private String lessonNumber;
    private String explanation;
    private boolean formal;

    public String getUnitNumber() {
        return unitNumber;
    }

    public void genUnitNumber() {
        this.unitNumber = CourseUtil.genSentenceUnitNumber();
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

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public List<String> getAudioName() {
        return audioName;
    }

    public void setAudioName(List<String> audioName) {
        this.audioName = audioName;
    }

    public SentenceLevel getComplexity() {
        return complexity;
    }

    public void setComplexity(SentenceLevel complexity) {
        this.complexity = complexity;
    }

    public String getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(String lessonNumber) {
        this.lessonNumber = lessonNumber;
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

    @Override
    public String toString() {
        return "Sentence{" +
                ", unitNumber='" + unitNumber + '\'' +
                ", content='" + content + '\'' +
                ", translation='" + translation + '\'' +
                ", audioName=" + audioName +
                ", complexity=" + complexity +
                ", lessonNumber='" + lessonNumber + '\'' +
                ", explanation='" + explanation + '\'' +
                ", formal=" + formal +
                '}';
    }
}
