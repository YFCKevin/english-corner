package com.gurula.talkyo.course;

import com.gurula.talkyo.course.enums.Level;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByLevel(Level chosenLevel);
}
