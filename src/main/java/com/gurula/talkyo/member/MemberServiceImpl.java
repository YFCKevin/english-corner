package com.gurula.talkyo.member;

import com.gurula.talkyo.azureai.PartnerRepository;
import com.gurula.talkyo.exception.ResultStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PartnerRepository partnerRepository;

    public MemberServiceImpl(MemberRepository memberRepository, PartnerRepository partnerRepository) {
        this.memberRepository = memberRepository;
        this.partnerRepository = partnerRepository;
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
}
