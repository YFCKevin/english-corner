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
    private String conversationId;
    private boolean finish;

    public LearningRecord(RecordDTO recordDTO, boolean finish, String memberId) {
        this.courseId = recordDTO.getCourseId();
        this.conversationId = recordDTO.getConversationId();
        this.lessonId = recordDTO.getLessonId();
        this.finish = finish;
        this.memberId = memberId;
    }

    public LearningRecord(LearningRecord record, boolean finish) {
        this.id = record.getId();
        this.memberId = record.getMemberId();
        this.lessonId = record.getLessonId();
        this.conversationId = record.getConversationId();
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

    public String getConversationId() {
        return conversationId;
    }

    public boolean isFinish() {
        return finish;
    }
}
