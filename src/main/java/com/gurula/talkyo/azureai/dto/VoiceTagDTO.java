package com.gurula.talkyo.azureai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gurula.talkyo.azureai.enums.TailoredScenario;
import com.gurula.talkyo.azureai.enums.VoicePersonality;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoiceTagDTO {
    @JsonProperty("TailoredScenarios")
    private List<TailoredScenario> tailoredScenarios;

    @JsonProperty("VoicePersonalities")
    private List<VoicePersonality> voicePersonalities;

    public List<TailoredScenario> getTailoredScenarios() {
        return tailoredScenarios;
    }

    public void setTailoredScenarios(List<TailoredScenario> tailoredScenarios) {
        this.tailoredScenarios = tailoredScenarios;
    }

    public List<VoicePersonality> getVoicePersonalities() {
        return voicePersonalities;
    }

    public void setVoicePersonalities(List<VoicePersonality> voicePersonalities) {
        this.voicePersonalities = voicePersonalities;
    }
}
