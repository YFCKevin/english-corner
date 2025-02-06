package com.gurula.talkyo.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.course.dto.CourseRequestDTO;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/course")
public class CourseController {
    private final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/admin/import")
    public ResponseEntity<?> importCourse (@RequestBody CourseRequestDTO dto) throws ExecutionException, JsonProcessingException, InterruptedException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [importCourse]", member.getName(), member.getId());

        ResultStatus resultStatus = new ResultStatus();

        Map<String, String> courseLessonMap = courseService.importCourse(dto, member);
        final String courseId = courseLessonMap.get("courseId");
        final String lessonId = courseLessonMap.get("lessonId");
        if (StringUtils.isNotBlank(courseId)) {
            // 產生sentence的翻譯和語音
            final ResultStatus result = courseService.genTranslationAndAudio(courseId, lessonId);

            resultStatus.setCode(result.getCode());
            resultStatus.setMessage(result.getMessage());
        }
        return ResponseEntity.ok(resultStatus);
    }
}
