package com.gurula.talkyo.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.course.dto.CourseRequestDTO;
import com.gurula.talkyo.course.dto.LessonDTO;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.openai.LLMService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService{
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LLMService llmService;
    private final SimpleDateFormat sdf;
    private final ObjectMapper objectMapper;
    private final AudioService audioService;

    public CourseServiceImpl(CourseRepository courseRepository, LessonRepository lessonRepository, LLMService llmService, @Qualifier("sdf") SimpleDateFormat sdf, ObjectMapper objectMapper, AudioService audioService) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.llmService = llmService;
        this.sdf = sdf;
        this.objectMapper = objectMapper;
        this.audioService = audioService;
    }

    @Transactional
    @Override
    public String importCourse(CourseRequestDTO dto, Member member) {
        Course course = new Course();
        course.setTopic(dto.getTopic());
        course.setLevel(dto.getLevel());
        course.setCreationDate(sdf.format(new Date()));
        course.setCreator(member.getId());
        final Course savedCourse = courseRepository.save(course);

        final List<LessonDTO> lessons = dto.getLessons();
        final List<Lesson> lessonList = lessons.stream().map(l -> {
            Lesson lesson = new Lesson();
            lesson.setCourseId(savedCourse.getId());
            lesson.setName(l.getName());
            lesson.genLessonNumber();
            final List<Sentence> sentenceList = l.getSentences().stream().map(s -> {
                Sentence sentence = new Sentence();
                sentence.genUnitNumber();
                sentence.setContent(s.getContent());
                sentence.setComplexity(s.getComplexity());
                sentence.setLessonNumber(lesson.getLessonNumber());
                return sentence;
            }).toList();
            lesson.setSentences(sentenceList);
            return lesson;
        }).toList();
        lessonRepository.saveAll(lessonList);

        return savedCourse.getId();
    }

    @Override
    @Transactional
    public ResultStatus genTranslationAndAudio(String courseId) throws JsonProcessingException, ExecutionException, InterruptedException {
        ResultStatus resultStatus = new ResultStatus();
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        if (lessons.size() == 0) {
            resultStatus.setMessage("C002");
            resultStatus.setMessage("查無情境單元");
        } else {
            Map<String, String> allSentencesMap = lessons.stream()
                    .flatMap(lesson -> lesson.getSentences().stream())
                    .collect(Collectors.toMap(Sentence::getUnitNumber, Sentence::getContent, (existing, replacement) -> existing));

            final String translationJson = llmService.translateSentence(objectMapper.writeValueAsString(allSentencesMap));

            List<Sentence> sentences = objectMapper.readValue(translationJson, new TypeReference<>() {});

            sentences = sentences.stream().map(sentence -> {
                try {
                    final List<String> audioPaths = audioService.speechSynthesis(sentence.getContent(), sentence.getUnitNumber());
                    sentence.setAudioPath(audioPaths);
                    return sentence;
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).toList();

            final Map<String, List<Sentence>> groupingByLessonNumber = sentences.stream().collect(Collectors.groupingBy(Sentence::getLessonNumber));
            lessons = lessons.stream().peek(lesson -> {
                final List<Sentence> finalSentences = groupingByLessonNumber.get(lesson.getLessonNumber());
                lesson.setSentences(finalSentences);
            }).toList();

            lessonRepository.saveAll(lessons);

            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
        }

        return resultStatus;
    }
}
