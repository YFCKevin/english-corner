package com.gurula.talkyo.member;

import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.exception.ResultStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class MemberController {
    private final ConfigProperties configProperties;

    public MemberController(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @GetMapping("/memberInfo")
    public Member memberInfo () {
        final Member member = MemberContext.getMember();
        return Objects.requireNonNullElseGet(member, Member::new);
    }


    @GetMapping("/logout")
    public ResponseEntity<?> logout (){
        MemberContext.removeMember();
        ResultStatus resultStatus = new ResultStatus();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(configProperties.getGlobalDomain() + "index.html");
        return ResponseEntity.ok(resultStatus);
    }
}
