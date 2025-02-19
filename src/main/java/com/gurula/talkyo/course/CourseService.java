package com.gurula.talkyo.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.course.dto.CourseRequestDTO;
import com.gurula.talkyo.course.dto.LessonInfoDTO;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface CourseService {
    Map<String, String> importCourse(CourseRequestDTO dto, Member member);

    ResultStatus genTranslationAndAudio(String courseId, String lessonId) throws JsonProcessingException, ExecutionException, InterruptedException;

    LessonInfoDTO getLessonInfo(String lessonId, String partnerId);
}
