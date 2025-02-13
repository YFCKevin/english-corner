package com.gurula.talkyo.member;

import com.gurula.talkyo.member.dto.LearningPlanDTO;
import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.exception.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final Logger logger = LoggerFactory.getLogger(MemberController.class);
    private final ConfigProperties configProperties;
    private final MemberService memberService;

    public MemberController(ConfigProperties configProperties, MemberService memberService) {
        this.configProperties = configProperties;
        this.memberService = memberService;
    }

    @GetMapping("/info")
    public Member info () {
        final Member member = MemberContext.getMember();
        return Objects.requireNonNullElseGet(member, Member::new);
    }


    @PatchMapping("/choosePartner/{id}")
    public ResponseEntity<?> choosePartner (@PathVariable String id){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [choosePartner] [{}]", member.getName(), member.getId(), id);

        ResultStatus<Void> resultStatus = new ResultStatus<>();
        ResultStatus<Void> result = memberService.choosePartner(id);
        resultStatus.setCode(result.getCode());
        resultStatus.setMessage(result.getMessage());
        return ResponseEntity.ok(resultStatus);
    }


    @GetMapping("/projects")
    public ResponseEntity<?> projects (){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [projects]", member.getName(), member.getId());

        List<LearningPlanDTO> learningPlanDTOList = memberService.getMyLearningPlans(member);

        ResultStatus<List<LearningPlanDTO>> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(learningPlanDTOList);
        return ResponseEntity.ok(resultStatus);
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
