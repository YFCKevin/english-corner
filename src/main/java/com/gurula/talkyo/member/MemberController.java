package com.gurula.talkyo.member;

import com.gurula.talkyo.azureai.Partner;
import com.gurula.talkyo.azureai.PartnerRepository;
import com.gurula.talkyo.azureai.dto.PartnerResponseDTO;
import com.gurula.talkyo.azureai.enums.TailoredScenario;
import com.gurula.talkyo.azureai.enums.VoicePersonality;
import com.gurula.talkyo.member.dto.LearningPlanDTO;
import com.gurula.talkyo.member.dto.MemberDTO;
import com.gurula.talkyo.member.dto.ProfileDTO;
import com.gurula.talkyo.member.enums.Role;
import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.exception.ResultStatus;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/member")
public class MemberController {
    private final Logger logger = LoggerFactory.getLogger(MemberController.class);
    private final ConfigProperties configProperties;
    private final MemberService memberService;
    private final PartnerRepository partnerRepository;

    public MemberController(ConfigProperties configProperties, MemberService memberService,
                            PartnerRepository partnerRepository) {
        this.configProperties = configProperties;
        this.memberService = memberService;
        this.partnerRepository = partnerRepository;
    }

    @GetMapping("/info")
    public ResponseEntity<?> info () {
        final Member member = MemberContext.getMember();
        final Optional<Partner> partnerOpt = partnerRepository.findById(member.getPartnerId());
        if (partnerOpt.isPresent()) {
            final Partner partner = partnerOpt.get();
            PartnerResponseDTO partnerResponseDTO = new PartnerResponseDTO(
                    partner.getId(),
                    partner.getDisplayName(),
                    partner.getGender(),
                    partner.getLocale().getLabel(),
                    partner.getVoiceTag() != null ? partner.getVoiceTag().getVoicePersonalities().stream().map(VoicePersonality::getLabel).toList() : null,
                    partner.getVoiceTag() != null ? partner.getVoiceTag().getTailoredScenarios().stream().map(TailoredScenario::getLabel).toList() : null,
                    configProperties.getPicShowPath() + "partner/" + partner.getCoverName(),
                    partner.getShortName()
            );
            MemberDTO memberDTO = new MemberDTO(
                    member.getId(),
                    member.getUserId(),
                    member.getPictureUrl(),
                    member.getCoverName(),
                    member.getName(),
                    member.getEmail(),
                    member.getChosenLevel(),
                    partnerResponseDTO,
                    member.getRole().getLabel()
            );
            return ResponseEntity.ok(Objects.requireNonNullElseGet(memberDTO, MemberDTO::new));
        }
        return ResponseEntity.ok(new MemberDTO());
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


    @GetMapping("/project/finish/{lessonId}")
    public ResponseEntity<?> finishedProjects (@PathVariable String lessonId){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [finished project]", member.getName(), member.getId());

        List<LearningPlanDTO> learningPlanDTOList = memberService.getFinishedProjects(member.getId(), lessonId);

        ResultStatus<List<LearningPlanDTO>> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(learningPlanDTOList);

        return ResponseEntity.ok(resultStatus);
    }



    @GetMapping("/profile")
    public ResponseEntity<?> profile (){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [profile]", member.getName(), member.getId());

        ProfileDTO profileDTO = memberService.profile(member);

        ResultStatus<ProfileDTO> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(profileDTO);
        return ResponseEntity.ok(resultStatus);
    }



    @PatchMapping("/exp/add/{point}")
    public ResponseEntity<?> addExp (@PathVariable int point){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [add exp]", member.getName(), member.getId());

        memberService.addExp(member, point);

        ResultStatus<Void> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        return ResponseEntity.ok(resultStatus);
    }


    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        MemberContext.removeMember();

        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", "")
                .secure(true)
                .path("/")
                .maxAge(0)   // 365天
                .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        return "redirect:" + configProperties.getGlobalDomain() + "sign-in.html";
    }
}
