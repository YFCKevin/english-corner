package com.gurula.talkyo.member;

import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.dto.LearningPlanDTO;
import com.gurula.talkyo.member.dto.ProfileDTO;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Optional<Member> findByEmail(String email);

    Member save(Member member);

    Optional<Member> findByUserId(String userId);

    Optional<Member> findById(String memberId);

    ResultStatus<Void> choosePartner(String id);

    List<LearningPlanDTO> getMyLearningPlans(Member member);

    List<LearningPlanDTO> getFinishedProjects(String memberId, String lessonId);

    ProfileDTO profile(Member member);

    void addExp(Member member, int point);
}
