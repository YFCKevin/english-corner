package com.gurula.talkyo.course.dto;

import com.gurula.talkyo.course.enums.Level;

import java.util.ArrayList;
import java.util.List;

public class CourseRequestDTO {
    private String topic;
    private Level level;
    private List<LessonDTO> lessons = new ArrayList<>();

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<LessonDTO> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonDTO> lessons) {
        this.lessons = lessons;
    }
}
