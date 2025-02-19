package com.gurula.talkyo.course.dto;

import com.gurula.talkyo.course.Sentence;

import java.util.ArrayList;
import java.util.List;

public class LessonInfoDTO {
    private String lessonNumber;
    private String name;
    private String coverName;   // 封面照名稱
    private String desc;        // 情境單元描述
    private List<Sentence> sentences = new ArrayList<>();
    private String courseTopic;

    public LessonInfoDTO() {
    }

    public LessonInfoDTO(String lessonNumber, String name, String coverName, String desc, List<Sentence> sentences, String courseTopic) {
        this.lessonNumber = lessonNumber;
        this.name = name;
        this.coverName = coverName;
        this.desc = desc;
        this.sentences = sentences;
        this.courseTopic = courseTopic;
    }

    public String getLessonNumber() {
        return lessonNumber;
    }

    public String getName() {
        return name;
    }

    public String getCoverName() {
        return coverName;
    }

    public String getDesc() {
        return desc;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public String getCourseTopic() {
        return courseTopic;
    }
}
