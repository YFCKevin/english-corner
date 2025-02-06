package com.gurula.talkyo.course.dto;

import java.util.ArrayList;
import java.util.List;

public class LessonDTO {
    private String name;
    private String desc;
    private String coverName;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCoverName() {
        return coverName;
    }

    public void setCoverName(String coverName) {
        this.coverName = coverName;
    }
}
