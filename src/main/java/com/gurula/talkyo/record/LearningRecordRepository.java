package com.gurula.talkyo.record;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LearningRecordRepository extends MongoRepository<LearningRecord, String> {
    Optional<LearningRecord> findByLessonId(String lessonId);
    Optional<LearningRecord> findByChatroomId(String chatroomId);

}
