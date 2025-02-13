package com.gurula.talkyo.member;

import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.dto.LearningPlanDTO;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Optional<Member> findByEmail(String email);

    Member save(Member member);

    Optional<Member> findByUserId(String userId);

    Optional<Member> findById(String memberId);

    ResultStatus<Void> choosePartner(String id);

    List<LearningPlanDTO> getMyLearningPlans(Member member);
}
