package com.gurula.talkyo.openai.dto;

import com.gurula.talkyo.chatroom.Scenario;

public class LLMChatRequestDTO {
    private Scenario scenario;
    private String sentences;
    private String historyMsgs;

    public LLMChatRequestDTO(Scenario scenario, String sentences) {
        this.scenario = scenario;
        this.sentences = sentences;
    }

    public LLMChatRequestDTO(String historyMsgs, Scenario scenario) {
        this.historyMsgs = historyMsgs;
        this.scenario = scenario;
    }

    public LLMChatRequestDTO(Scenario scenario) {
        this.scenario = scenario;
    }

    public LLMChatRequestDTO(String historyMsgs) {
        this.historyMsgs = historyMsgs;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public String getSentences() {
        return sentences;
    }

    public String getHistoryMsgs() {
        return historyMsgs;
    }
}
