package com.gurula.talkyo.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gurula.talkyo.azureai.AudioService;
import com.gurula.talkyo.azureai.Partner;
import com.gurula.talkyo.azureai.PartnerRepository;
import com.gurula.talkyo.course.dto.CourseRequestDTO;
import com.gurula.talkyo.course.dto.LessonDTO;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.openai.LLMService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService{
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LLMService llmService;
    private final SimpleDateFormat sdf;
    private final ObjectMapper objectMapper;
    private final AudioService audioService;
    private final PartnerRepository partnerRepository;

    public CourseServiceImpl(CourseRepository courseRepository, LessonRepository lessonRepository, LLMService llmService, @Qualifier("sdf") SimpleDateFormat sdf, ObjectMapper objectMapper, AudioService audioService,
                             PartnerRepository partnerRepository) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.llmService = llmService;
        this.sdf = sdf;
        this.objectMapper = objectMapper;
        this.audioService = audioService;
        this.partnerRepository = partnerRepository;
    }

    @Transactional
    @Override
    public Map<String, String> importCourse(CourseRequestDTO dto, Member member) {

        final String courseId = dto.getCourseId();
        Course newCourse = null;
        if (StringUtils.isBlank(courseId)) {
            Course course = new Course();
            course.setTopic(dto.getTopic());
            course.setLevel(dto.getLevel());
            course.setCreationDate(sdf.format(new Date()));
            course.setCreator(member.getId());
            newCourse = courseRepository.save(course);
        } else {
            newCourse = courseRepository.findById(courseId).get();
        }

        final List<LessonDTO> lessons = dto.getLessons();
        Course finalNewCourse = newCourse;
        final List<Lesson> lessonList = lessons.stream().map(l -> {
            Lesson lesson = new Lesson();
            lesson.setCourseId(finalNewCourse.getId());
            lesson.setName(l.getName());
            lesson.genLessonNumber();
            lesson.setDesc(l.getDesc());
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
        final List<Lesson> savedLessons = lessonRepository.saveAll(lessonList);

        Map<String, String> courseLessonMap = new HashMap<>();
        courseLessonMap.put("courseId", newCourse.getId());
        courseLessonMap.put("lessonId", savedLessons.get(0).getId());

        return courseLessonMap;
    }


    /**
     * 一次至多一個 course, lesson 上傳
     * @param courseId
     * @return
     * @throws JsonProcessingException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    @Transactional
    public ResultStatus<Void> genTranslationAndAudio(String courseId, String lessonId) throws JsonProcessingException, ExecutionException, InterruptedException {
        ResultStatus<Void> resultStatus = new ResultStatus<>();
        final Course course = courseRepository.findById(courseId).get();
        List<Lesson> lessons = lessonRepository.findByCourseIdAndId(courseId, lessonId);
        if (lessons.size() == 0) {
            resultStatus.setMessage("C002");
            resultStatus.setMessage("查無情境單元");
        } else {
            Map<String, String> allSentencesMap = lessons.stream()
                    .flatMap(lesson -> lesson.getSentences().stream())
                    .collect(Collectors.toMap(Sentence::getUnitNumber, Sentence::getContent, (existing, replacement) -> existing));

            final String translationJson = llmService.translateSentence(objectMapper.writeValueAsString(allSentencesMap));

            List<Sentence> sentences = objectMapper.readValue(translationJson, new TypeReference<>() {});

            final List<Partner> partners = partnerRepository.findByDisplayNameIn(
                    Arrays.asList(
                            "Neerja", "Aarav",
                            "Ada Multilingual", "Ollie Multilingual",
                            "Emma Multilingual", "Brian Multilingual", "Jane", "Jason",
                            "Guy", "Brandon", "Christopher", "Cora", "Jenny Multilingual",
                            "Ryan Multilingual"
                    )
            );
            List<Lesson> finalLessons = lessons;
            sentences = sentences.stream().map(sentence -> {
                try {
                    List<Path> audioPaths = new ArrayList<>();
                    for (Partner partner : partners) {
                        Path audioPath = audioService.speechSynthesis(course.getTopic(), finalLessons.get(0).getLessonNumber(), sentence.getContent(), sentence.getUnitNumber(), partner.getShortName());
                        audioPaths.add(audioPath);
                    }
                    final List<String> audioFileNames = audioPaths.stream().map(path -> path.getFileName().toString()).toList();
                    sentence.setAudioName(audioFileNames);
                    return sentence;
                } catch (ExecutionException | InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();

            Map<String, Sentence> sentenceMap = lessons.stream()
                    .flatMap(lesson -> lesson.getSentences().stream())
                    .collect(Collectors.toMap(Sentence::getUnitNumber, Function.identity()));

            sentences.forEach(sentence -> {
                Sentence correspondingSentence = sentenceMap.get(sentence.getUnitNumber());
                if (correspondingSentence != null) {
                    sentence.setLessonNumber(correspondingSentence.getLessonNumber());
                }
            });

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
