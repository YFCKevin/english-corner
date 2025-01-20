package com.gurula.talkyo.course.dto;

import java.util.ArrayList;
import java.util.List;

public class LessonDTO {
    private String name;
    private List<SentenceDTO> sentences = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SentenceDTO> getSentences() {
        return sentences;
    }

    public void setSentences(List<SentenceDTO> sentences) {
        this.sentences = sentences;
    }
}
