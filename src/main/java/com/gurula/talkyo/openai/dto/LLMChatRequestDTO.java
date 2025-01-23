package com.gurula.talkyo.openai.dto;

import com.gurula.talkyo.chatroom.Scenario;

public class LLMChatRequestDTO {
    private Scenario scenario;
    private String sentences;

    public LLMChatRequestDTO(Scenario scenario, String sentences) {
        this.scenario = scenario;
        this.sentences = sentences;
    }

    public LLMChatRequestDTO(Scenario scenario) {
        this.scenario = scenario;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public String getSentences() {
        return sentences;
    }
}
