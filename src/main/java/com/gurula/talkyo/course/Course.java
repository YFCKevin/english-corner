package com.gurula.talkyo.course;

import com.gurula.talkyo.course.enums.Level;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "course")
public class Course {
    @Id
    private String id;
    private String topic;
    private Level level;
    private String creationDate;
    private String modificationDate;
    private String deletionDate;
    private String creator;
    private String editor;

    public Course(){}

    public String getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(String deletionDate) {
        this.deletionDate = deletionDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }
}
