package com.gurula.talkyo.course;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface LessonRepository extends MongoRepository<Lesson, String> {

    List<Lesson> findByCourseIdAndId(String courseId, String lessonId);

    List<Lesson> findByCourseIdIn(List<String> courseIds);
}
