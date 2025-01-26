package com.gurula.talkyo.azureai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gurula.talkyo.azureai.Partner;
import com.gurula.talkyo.azureai.VoiceTag;
import com.gurula.talkyo.azureai.enums.Locale;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerDTO {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("DisplayName")
    private String displayName;

    @JsonProperty("LocalName")
    private String localName;

    @JsonProperty("ShortName")
    private String shortName;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("Locale")
    private Locale locale;

    @JsonProperty("LocaleName")
    private String localeName;

    @JsonProperty("SecondaryLocaleList")
    private List<String> secondaryLocaleList;

    @JsonProperty("SampleRateHertz")
    private String sampleRateHertz;

    @JsonProperty("VoiceType")
    private String voiceType;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("VoiceTag")
    private VoiceTagDTO voiceTag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public List<String> getSecondaryLocaleList() {
        return secondaryLocaleList;
    }

    public void setSecondaryLocaleList(List<String> secondaryLocaleList) {
        this.secondaryLocaleList = secondaryLocaleList;
    }

    public String getSampleRateHertz() {
        return sampleRateHertz;
    }

    public void setSampleRateHertz(String sampleRateHertz) {
        this.sampleRateHertz = sampleRateHertz;
    }

    public String getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VoiceTagDTO getVoiceTag() {
        return voiceTag;
    }

    public void setVoiceTag(VoiceTagDTO voiceTag) {
        this.voiceTag = voiceTag;
    }

    public static Partner convertToPartner(PartnerDTO partnerDTO, String today) {
        Partner partner = new Partner();

        partner.setName(partnerDTO.getName());
        partner.setDisplayName(partnerDTO.getDisplayName());
        partner.setLocalName(partnerDTO.getLocalName());
        partner.setShortName(partnerDTO.getShortName());
        partner.setGender(partnerDTO.getGender());
        partner.setLocale(partnerDTO.getLocale());
        partner.setLocaleName(partnerDTO.getLocaleName());
        partner.setSecondaryLocaleList(partnerDTO.getSecondaryLocaleList());
        partner.setSampleRateHertz(partnerDTO.getSampleRateHertz());
        partner.setVoiceType(partnerDTO.getVoiceType());
        partner.setStatus(partnerDTO.getStatus());
        partner.setCreationDate(today);

        if (partnerDTO.getVoiceTag() != null) {
            VoiceTag voiceTag = new VoiceTag();
            voiceTag.setTailoredScenarios(partnerDTO.getVoiceTag().getTailoredScenarios());
            voiceTag.setVoicePersonalities(partnerDTO.getVoiceTag().getVoicePersonalities());
            partner.setVoiceTag(voiceTag);
        }

        return partner;
    }
}
