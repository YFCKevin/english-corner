package com.gurula.talkyo.azureai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gurula.talkyo.azureai.dto.PartnerDTO;
import com.gurula.talkyo.azureai.dto.PartnerResponseDTO;
import com.gurula.talkyo.azureai.enums.TailoredScenario;
import com.gurula.talkyo.azureai.enums.VoicePersonality;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberContext;
import com.gurula.talkyo.properties.AzureProperties;
import com.gurula.talkyo.properties.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.gurula.talkyo.azureai.dto.PartnerDTO.convertToPartner;

@RestController
@RequestMapping("/partner")
public class PartnerController {
    private final Logger logger = LoggerFactory.getLogger(PartnerController.class);
    private final SimpleDateFormat sdf;
    private final PartnerService partnerService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AzureProperties azureProperties;
    private final ConfigProperties configProperties;

    public PartnerController(@Qualifier("sdf") SimpleDateFormat sdf, PartnerService partnerService, RestTemplate restTemplate, ObjectMapper objectMapper, AzureProperties azureProperties, ConfigProperties configProperties) {
        this.sdf = sdf;
        this.partnerService = partnerService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.azureProperties = azureProperties;
        this.configProperties = configProperties;
    }

    @PostMapping("/admin/save")
    public ResponseEntity<?> savePartner () throws JsonProcessingException {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [savePartner]", member.getName(), member.getId());

        ResultStatus<List<Partner>> resultStatus = new ResultStatus<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", azureProperties.getAudio().getKey());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "https://" + azureProperties.getAudio().getRegion() + ".tts.speech.microsoft.com/cognitiveservices/voices/list";

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCodeValue() == 200) {
            List<PartnerDTO> partnerDTOS = objectMapper
                    .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
                    .readValue(response.getBody(), new TypeReference<>() {});

            final Set<String> existingShortNames = partnerService.findAll().stream()
                    .map(Partner::getShortName).collect(Collectors.toSet());

            List<Partner> newPartners = partnerDTOS.stream()
                    .filter(partnerDTO -> !existingShortNames.contains(partnerDTO.getShortName()))
                    .map(partnerDTO -> convertToPartner(partnerDTO, sdf.format(new Date())))
                    .toList();

            final List<Partner> savedPartners = partnerService.saveAll(newPartners);

            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
            resultStatus.setData(savedPartners);
        }
        return ResponseEntity.ok(resultStatus);
    }


    @GetMapping("/list")
    public ResponseEntity<?> partnerList (){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [partnerList]", member.getName(), member.getId());

        ResultStatus<List<PartnerResponseDTO>> resultStatus = new ResultStatus<>();

        List<Partner> partners = partnerService.getPartnerList();

        List<PartnerResponseDTO> partnerResponseDTOList = new ArrayList<>();

        partners.forEach(partner -> {
            PartnerResponseDTO partnerResponseDTO = new PartnerResponseDTO(
                    partner.getId(),
                    partner.getDisplayName(),
                    partner.getGender(),
                    partner.getLocale().getLabel(),
                    partner.getVoiceTag() != null ? partner.getVoiceTag().getVoicePersonalities().stream().map(VoicePersonality::getLabel).toList() : Collections.singletonList(""),
                    partner.getVoiceTag() != null ? partner.getVoiceTag().getTailoredScenarios().stream().map(TailoredScenario::getLabel).toList() : Collections.singletonList(""),
                    configProperties.getPicShowPath() + "partner/" + partner.getCoverName(),
                    partner.getShortName()
            );
            partnerResponseDTOList.add(partnerResponseDTO);
        });

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(partnerResponseDTOList);
        return ResponseEntity.ok(resultStatus);
    }
}
