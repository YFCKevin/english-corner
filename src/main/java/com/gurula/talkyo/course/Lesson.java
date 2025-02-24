package com.gurula.talkyo.course;

import com.gurula.talkyo.chatroom.Scenario;
import com.gurula.talkyo.course.utils.CourseUtil;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    @Id
    private String id;
    private String lessonNumber;
    private String name;
    private String coverName;   // 封面照名稱
    private String desc;        // 情境單元描述
    private List<Sentence> sentences = new ArrayList<>();
    private String courseId;
    private Scenario scenario;

    public Lesson(){}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLessonNumber() {
        return lessonNumber;
    }

    public void genLessonNumber() {
        this.lessonNumber = CourseUtil.genLessonNumber();
    }

    public String getCoverName() {
        return coverName;
    }

    public void setCoverName(String coverName) {
        this.coverName = coverName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }
}
