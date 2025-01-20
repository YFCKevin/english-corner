package com.gurula.talkyo.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.course.dto.CourseRequestDTO;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;

import java.util.concurrent.ExecutionException;

public interface CourseService {
    String importCourse(CourseRequestDTO dto, Member member);

    ResultStatus genTranslationAndAudio(String courseId) throws JsonProcessingException, ExecutionException, InterruptedException;
}
