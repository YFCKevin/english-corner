package com.gurula.talkyo.azureai.dto;

import com.gurula.talkyo.azureai.enums.Locale;

import java.util.List;

public class PartnerResponseDTO {
    private String id;
    private String displayName;
    private String gender;
    private String locale;
    private List<String> voicePersonality;
    private List<String> tailoredScenario;
    private String coverPath;
    private String shortName;

    public PartnerResponseDTO() {
    }

    public PartnerResponseDTO(String id, String displayName, String gender, String locale, List<String> voicePersonality, List<String> tailoredScenario, String coverPath, String shortName) {
        this.id = id;
        this.displayName = displayName;
        if ("Male".equals(gender)) {
            this.gender = "男";
        } else if ("Female".equals(gender)) {
            this.gender = "女";
        }
        this.locale = locale;
        this.voicePersonality = voicePersonality;
        this.tailoredScenario = tailoredScenario;
        this.coverPath = coverPath;
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGender() {
        return gender;
    }

    public String getLocale() {
        return locale;
    }

    public List<String> getVoicePersonality() {
        return voicePersonality;
    }

    public List<String> getTailoredScenario() {
        return tailoredScenario;
    }

    public String getCoverPath() {
        return coverPath;
    }
}
