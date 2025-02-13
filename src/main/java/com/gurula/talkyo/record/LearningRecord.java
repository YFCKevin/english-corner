package com.gurula.talkyo.record;

import com.gurula.talkyo.record.dto.RecordDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "learning_record")
public class LearningRecord {
    @Id
    private String id;
    private String memberId;
    private String courseId;
    private String lessonId;
    private String chatroomId;
    private boolean finish;

    public LearningRecord() {
    }

    public LearningRecord(RecordDTO recordDTO, boolean finish, String memberId) {
        this.courseId = (recordDTO.getCourseId() != null) ? recordDTO.getCourseId() : null;
        this.chatroomId = recordDTO.getChatroomId();
        this.lessonId = (recordDTO.getLessonId() != null) ? recordDTO.getLessonId() : null;
        this.finish = finish;
        this.memberId = memberId;
    }

    public LearningRecord(LearningRecord record, boolean finish) {
        this.id = record.getId();
        this.memberId = record.getMemberId();
        this.lessonId = record.getLessonId();
        this.chatroomId = record.getChatroomId();
        this.courseId = record.getCourseId();
        this.finish = finish;
    }

    public String getId() {
        return id;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public boolean isFinish() {
        return finish;
    }
}
