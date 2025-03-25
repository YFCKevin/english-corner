package com.gurula.talkyo.oauth;

import com.gurula.talkyo.course.enums.Level;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberService;
import com.gurula.talkyo.member.enums.Provider;
import com.gurula.talkyo.member.enums.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private final MemberService memberService;
    private final SimpleDateFormat sdf;

    public UserService(MemberService memberService, @Qualifier("sdf") SimpleDateFormat sdf) {
        this.memberService = memberService;
        this.sdf = sdf;
    }

    public Member processOAuthPostLogin(String email, String name, String oauth2ClientName) {

        //取得系統上是不是有這個帳號
        Optional<Member> opt = memberService.findByEmail(email);
        Member member = new Member();

        //取得是GOOGLE或FACEBOOK登入
        Provider authType = Provider.valueOf(oauth2ClientName.toUpperCase());
        System.out.println("authType==>" + authType);

        if (opt.isEmpty()) { //如果沒有註冊過就新增
            member.setName(name);
            member.setEmail(email);
            member.setCreationDate(sdf.format(new Date()));
            member.setProvider(authType);
            member.setRole(Role.STUDENT);
            member.setPartnerId("6795b2cd007e72369f0db8a6");    // 預設為 Jenny AI
            member.setChosenLevel(Level.EASY);                  // 預設為 EASY
            memberService.save(member);
            System.out.println("尚未註冊");
        } else {
            member = opt.get();
        }
        return member;
    }

}
