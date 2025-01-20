package com.gurula.talkyo.course;

import com.gurula.talkyo.course.enums.SentenceLevel;
import com.gurula.talkyo.course.utils.CourseUtil;

import java.util.List;

public class Sentence {
    private String unitNumber;
    private String content;
    private String translation;
    private List<String> audioPath;
    private SentenceLevel complexity;
    private String lessonNumber;

    public String getUnitNumber() {
        return unitNumber;
    }

    public void genUnitNumber() {
        this.unitNumber = CourseUtil.genSentenceUnitNumber();
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

    public List<String> getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(List<String> audioPath) {
        this.audioPath = audioPath;
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
}
