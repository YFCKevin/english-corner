package com.gurula.talkyo.record;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;

public interface LearningRecordRepository extends MongoRepository<LearningRecord, String> {

    Optional<LearningRecord> findByChatroomId(String chatroomId);

    Set<LearningRecord> findByChatroomIdIn(Set<String> chatroomIds);
}
