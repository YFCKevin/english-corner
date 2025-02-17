package com.gurula.talkyo.member;

import com.gurula.talkyo.azureai.Partner;
import com.gurula.talkyo.azureai.PartnerRepository;
import com.gurula.talkyo.azureai.dto.PartnerResponseDTO;
import com.gurula.talkyo.azureai.enums.TailoredScenario;
import com.gurula.talkyo.azureai.enums.VoicePersonality;
import com.gurula.talkyo.member.dto.LearningPlanDTO;
import com.gurula.talkyo.member.dto.MemberDTO;
import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.exception.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
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
    public MemberDTO info () {
        final Member member = MemberContext.getMember();
        final Optional<Partner> partnerOpt = partnerRepository.findById(member.getPartnerId());
        if (partnerOpt.isPresent()) {
            final Partner partner = partnerOpt.get();
            PartnerResponseDTO partnerResponseDTO = new PartnerResponseDTO(
                    partner.getId(),
                    partner.getDisplayName(),
                    partner.getGender(),
                    partner.getLocale().getLabel(),
                    partner.getVoiceTag().getVoicePersonalities().stream().map(VoicePersonality::getLabel).toList(),
                    partner.getVoiceTag().getTailoredScenarios().stream().map(TailoredScenario::getLabel).toList(),
                    configProperties.getPicShowPath() + "partner/" + partner.getCoverName()
            );
            MemberDTO memberDTO = new MemberDTO(
                    member.getId(),
                    member.getUserId(),
                    member.getPictureUrl(),
                    member.getCoverName(),
                    member.getName(),
                    member.getEmail(),
                    member.getChosenLevel(),
                    partnerResponseDTO
            );
            return Objects.requireNonNullElseGet(memberDTO, MemberDTO::new);
        }
        return new MemberDTO();
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


    @GetMapping("/logout")
    public ResponseEntity<?> logout (){
        MemberContext.removeMember();
        ResultStatus<String> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(configProperties.getGlobalDomain() + "index.html");
        return ResponseEntity.ok(resultStatus);
    }
}
