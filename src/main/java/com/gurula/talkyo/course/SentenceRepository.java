package com.gurula.talkyo.course;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SentenceRepository extends MongoRepository<Sentence, String> {
}
