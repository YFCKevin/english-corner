package com.gurula.talkyo.record.dto;

public class RecordDTO {
    private String courseId;
    private String lessonId;
    private String chatroomId;

    public RecordDTO() {
    }

    public RecordDTO(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public RecordDTO(String courseId, String lessonId, String chatroomId) {
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.chatroomId = chatroomId;
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
}
