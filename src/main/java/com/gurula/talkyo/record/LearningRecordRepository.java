package com.gurula.talkyo.record;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LearningRecordRepository extends MongoRepository<LearningRecord, String> {

    Optional<LearningRecord> findByChatroomId(String chatroomId);

    Set<LearningRecord> findByChatroomIdIn(Set<String> chatroomIds);

    List<LearningRecord> findByMemberIdAndLessonIdAndFinish(String memberId, String lessonId, boolean finish);

    List<LearningRecord> findByMemberIdAndFinish(String memberId, boolean finish);
}
