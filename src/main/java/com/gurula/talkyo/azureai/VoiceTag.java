package com.gurula.talkyo.azureai;

import com.gurula.talkyo.azureai.enums.TailoredScenario;
import com.gurula.talkyo.azureai.enums.VoicePersonality;

import java.util.List;

public class VoiceTag {
    private List<TailoredScenario> tailoredScenarios;
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
