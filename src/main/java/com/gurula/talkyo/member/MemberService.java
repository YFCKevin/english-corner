package com.gurula.talkyo.member;

import com.gurula.talkyo.exception.ResultStatus;

import java.util.Optional;

public interface MemberService {
    Optional<Member> findByEmail(String email);

    Member save(Member member);

    Optional<Member> findByUserId(String userId);

    Optional<Member> findById(String memberId);

    ResultStatus<Void> choosePartner(String id);
}
