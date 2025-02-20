package com.gurula.talkyo.member;

import com.gurula.talkyo.azureai.PartnerRepository;
import com.gurula.talkyo.azureai.utils.AudioUtil;
import com.gurula.talkyo.chatroom.*;
import com.gurula.talkyo.chatroom.enums.ChatroomType;
import com.gurula.talkyo.chatroom.enums.RoomStatus;
import com.gurula.talkyo.course.Course;
import com.gurula.talkyo.course.CourseRepository;
import com.gurula.talkyo.course.Lesson;
import com.gurula.talkyo.course.LessonRepository;
import com.gurula.talkyo.course.enums.LessonType;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.dto.LearningPlanDTO;
import com.gurula.talkyo.member.dto.ProfileDTO;
import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.record.LearningRecord;
import com.gurula.talkyo.record.LearningRecordRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PartnerRepository partnerRepository;
    private final ChatroomRepository chatroomRepository;
    private final LearningRecordRepository learningRecordRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final MessageRepository messageRepository;
    private final ConfigProperties configProperties;

    public MemberServiceImpl(MemberRepository memberRepository, PartnerRepository partnerRepository,
                             ChatroomRepository chatroomRepository,
                             LearningRecordRepository learningRecordRepository,
                             LessonRepository lessonRepository,
                             CourseRepository courseRepository,
                             MessageRepository messageRepository, ConfigProperties configProperties) {
        this.memberRepository = memberRepository;
        this.partnerRepository = partnerRepository;
        this.chatroomRepository = chatroomRepository;
        this.learningRecordRepository = learningRecordRepository;
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.messageRepository = messageRepository;
        this.configProperties = configProperties;
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public Optional<Member> findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }

    @Override
    public Optional<Member> findById(String memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    public ResultStatus<Void> choosePartner(String id) {
        ResultStatus<Void> resultStatus = new ResultStatus<>();
        partnerRepository.findById(id)
                .map(partner -> {
                    final Member member = MemberContext.getMember();
                    member.setPartnerId(id);
                    memberRepository.save(member);
                    resultStatus.setCode("C000");
                    resultStatus.setMessage("成功");
                    return resultStatus;
                })
                .orElseGet(() -> {
                    resultStatus.setCode("C003");
                    resultStatus.setMessage("查無語伴");
                    return resultStatus;
                });
        return resultStatus;
    }

    @Override
    public List<LearningPlanDTO> getMyLearningPlans(Member member) {

        List<Course> courses = courseRepository.findByLevel(member.getChosenLevel());
        final List<String> courseIds = courses.stream().map(Course::getId).toList();
        List<Lesson> lessons = lessonRepository.findByCourseIdIn(courseIds);

        // 取出所有進行中、完課的 chatroom
        final List<Chatroom> chatrooms = chatroomRepository.findByOwnerIdOrderByCreationDateAsc(member.getId());
        final Set<String> chatroomIds = chatrooms.stream().map(Chatroom::getId).collect(Collectors.toSet());
        Set<LearningRecord> learningRecords = learningRecordRepository.findByChatroomIdIn(chatroomIds);
        // 根據 chatroomId 和 lessonId 設定一個 Map
        final Map<String, String> chatroomLessonMap = learningRecords.stream()
                .collect(Collectors.toMap(LearningRecord::getChatroomId, LearningRecord::getLessonId));

        // 根據 lessonId 分組 chatrooms
        Map<String, List<Chatroom>> lessonChatroomMap = new HashMap<>();
        for (Chatroom chatroom : chatrooms) {
            final String lessonId = chatroomLessonMap.get(chatroom.getId());
            if (StringUtils.isNotBlank(lessonId)) {
                lessonChatroomMap.computeIfAbsent(lessonId, k -> new ArrayList<>())
                        .add(chatroom);
            }
        }

        List<LearningPlanDTO> learningPlanDTOList = new ArrayList<>();

        // 根據 lessonId 處理每個 chatroom
        for (Lesson lesson : lessons) {
            final List<Chatroom> lessonChatrooms = lessonChatroomMap.getOrDefault(lesson.getId(), new ArrayList<>());
            if (lessonChatrooms.size() > 0) {

                // 先過濾出有 closeDate 的 chatroom 並選擇最早的 closeDate
                Optional<Chatroom> earliestCloseDateChatroomOpt = lessonChatrooms.stream()
                        .filter(chatroom -> chatroom.getCloseDate() != null)
                        .min(Comparator.comparing(Chatroom::getCloseDate));

                if (earliestCloseDateChatroomOpt.isPresent()) { // 已完課
                    final Chatroom earliestCloseDateChatroom = earliestCloseDateChatroomOpt.get();
                    final ConversationScore conversationScore = earliestCloseDateChatroom.getReport().getConversationScore();
                    final double accuracy = conversationScore.getAccuracy();
                    final double completeness = conversationScore.getCompleteness();
                    final double fluency = conversationScore.getFluency();
                    final double prosody = conversationScore.getProsody();
                    final double overallRating = (accuracy + completeness + fluency + prosody) / 4;
                    LearningPlanDTO learningPlanDTO = new LearningPlanDTO(
                            lesson.getId(),
                            lesson.getName(),
                            "image/" + lesson.getLessonNumber() + "/" + lesson.getCoverName(),
                            earliestCloseDateChatroom.getId(),
                            earliestCloseDateChatroom.getCloseDate(),
                            overallRating,
                            LessonType.COMPLETED
                    );
                    learningPlanDTOList.add(learningPlanDTO);
                } else {    // 進行中
                    // 再過濾出沒有 closeDate 的 chatroom
                    Optional<Chatroom> chatroomWithoutCloseDateOpt = lessonChatrooms.stream()
                            .filter(chatroom -> chatroom.getCloseDate() == null)
                            .findFirst();
                    final Chatroom ongoingChatroom = chatroomWithoutCloseDateOpt.get();
                    LearningPlanDTO learningPlanDTO = new LearningPlanDTO(
                            lesson.getId(),
                            lesson.getName(),
                            "image/" + lesson.getLessonNumber() + "/" + lesson.getCoverName(),
                            ongoingChatroom.getId(),
                            ongoingChatroom.getCloseDate(),
                            -1,
                            LessonType.IN_PROGRESS
                    );
                    learningPlanDTOList.add(learningPlanDTO);
                }
            } else {    // 尚未開始
                LearningPlanDTO learningPlanDTO = new LearningPlanDTO(
                        lesson.getId(),
                        lesson.getName(),
                        "image/" + lesson.getLessonNumber() + "/" + lesson.getCoverName(),
                        null,
                        null,
                        -1,
                        LessonType.NOT_STARTED
                );
                learningPlanDTOList.add(learningPlanDTO);
            }
        }

        return learningPlanDTOList;
    }

    @Override
    public List<LearningPlanDTO> getFinishedProjects(String memberId, String lessonId) {
        List<LearningRecord> learningRecords = learningRecordRepository.findByMemberIdAndLessonIdAndFinish(memberId, lessonId, true);
        final Map<String, Chatroom> chatroomMap = chatroomRepository.findAllById(learningRecords.stream().map(LearningRecord::getChatroomId).toList()).stream()
                .collect(Collectors.toMap(Chatroom::getId, Function.identity()));

        List<LearningPlanDTO> learningPlanDTOList = new ArrayList<>();
        for (LearningRecord learningRecord : learningRecords) {
            LearningPlanDTO learningPlanDTO = new LearningPlanDTO(
                    lessonId,
                    learningRecord.getChatroomId(),
                    chatroomMap.get(learningRecord.getChatroomId()).getCloseDate()
            );
            learningPlanDTOList.add(learningPlanDTO);
        }

        return learningPlanDTOList;
    }

    @Override
    public ProfileDTO profile(Member member) {
        final String memberId = member.getId();
        final int totalExp = member.getTotalExp();

        // 已完成課程
        List<LearningRecord> learningRecords = learningRecordRepository.findByMemberIdAndFinish(memberId, true);
        final int finishedCourseSize = learningRecords.size();

        // 說話次數
        List<Message> messages = messageRepository.findBySenderAndAudioNameIsNotNull(memberId);
        final int speakingFrequency = messages.size();

        // 講話時長
        final List<String> audioFilePaths = messages.stream()
                .map(message -> configProperties.getAudioSavePath() + message.getChatroomId() + "/" + message.getAudioName())
                .toList();
        final String totalSpeakDurations = formatDuration(AudioUtil.getTotalAudioDuration(audioFilePaths));

        // 最佳連勝 and 當前連勝
        List<Chatroom> chatrooms = chatroomRepository.findByOwnerIdAndRoomStatusAndChatroomTypeInOrderByCloseDateAsc(memberId, RoomStatus.CLOSED, List.of(ChatroomType.PROJECT, ChatroomType.SITUATION, ChatroomType.IMAGE));
        final Map<String, Integer> bestAndCurrentStreaks = calculateStreaks(chatrooms.stream().map(Chatroom::getCloseDate).toList());
        final Integer bestStreak = bestAndCurrentStreaks.get("bestStreak");
        final Integer currentStreak = bestAndCurrentStreaks.get("currentStreak");

        return new ProfileDTO(
                finishedCourseSize,
                speakingFrequency,
                totalSpeakDurations,
                totalExp,
                currentStreak,
                bestStreak
        );
    }

    @Override
    public void addExp(Member member, int point) {
        int totalExp = member.getTotalExp();
        totalExp += point;
        member.setTotalExp(totalExp);
        memberRepository.save(member);
    }


    public static String formatDuration(double totalSeconds) {
        if (totalSeconds < 60) {
            return String.format("%d sec", (int) totalSeconds);
        } else if (totalSeconds < 3600) {
            long minutes = (long) (totalSeconds / 60);
            long remainingSeconds = (long) (totalSeconds % 60);
            return String.format("%d min %d sec", minutes, remainingSeconds);
        } else {
            long hours = (long) (totalSeconds / 3600);
            long minutes = (long) ((totalSeconds % 3600) / 60);
            return String.format("%d hr %d min", hours, minutes);
        }
    }

    public static Map<String, Integer> calculateStreaks(List<String> dateStrings) {
        if (dateStrings == null || dateStrings.isEmpty()) {
            return Map.of("bestStreak", 0, "currentStreak", 0);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<LocalDate> dates = new ArrayList<>();
        for (String dateString : dateStrings) {
            try {
                LocalDate date = LocalDate.parse(dateString, formatter);
                dates.add(date);
            } catch (Exception e) {
                System.err.println("Invalid date format: " + dateString);
            }
        }

        if (dates.isEmpty()) {
            return Map.of("bestStreak", 0, "currentStreak", 0);
        }

        Collections.sort(dates);

        int bestStreak = 1;
        int currentStreak = 1;
        int maxBestStreak = 1;

        //Calculate best streak
        for (int i = 1; i < dates.size(); i++) {
            LocalDate currentDate = dates.get(i);
            LocalDate previousDate = dates.get(i - 1);

            if (previousDate.plusDays(1).equals(currentDate)) {
                bestStreak++;
                maxBestStreak = Math.max(maxBestStreak, bestStreak);
            } else if (!currentDate.equals(previousDate)) {
                bestStreak = 1;
            }
        }

        //Calculate current streak
        LocalDate today = LocalDate.now();
        for (int i = dates.size() - 1; i >= 0; i--) {
            LocalDate currentDate = dates.get(i);
            if (currentDate.equals(today) || currentDate.isBefore(today)) {
                if (currentDate.plusDays(1).equals(today) || currentDate.equals(today)) {
                    currentStreak++;
                } else {
                    break;
                }
                today = currentDate;
            }
        }

        return Map.of("bestStreak", maxBestStreak, "currentStreak", currentStreak - 1);
    }
}
