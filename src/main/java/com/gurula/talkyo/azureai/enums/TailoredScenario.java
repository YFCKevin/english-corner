package com.gurula.talkyo.azureai.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TailoredScenario {
    E_LEARNING(1, "電子學習"),
    MEDITATION(2, "冥想"),
    NARRATION(3, "旁白"),
    SOCIAL_MEDIA(4, "社交媒體"),
    AUDIOBOOKS(5, "有聲書"),
    PODCAST(6, "播客"),
    AUDIOBOOK(7, "有聲書"),
    DOCUMENTARY(8, "紀錄片"),
    ASSISTANT(9, "助手"),
    MEDITATION_LOWERCASE(10, "冥想"),
    NEWS(11, "新聞"),
    CHAT(12, "聊天"),
    ADVERTISEMENT(13, "廣告"),
    GAMING(14, "遊戲");

    private int value;
    private String label;
    private TailoredScenario(int value,String label){
        this.value = value;
        this.label = label;
    }
    public int getValue() {
        return value;
    }
    private void setValue(int value) {
        this.value = value;
    }
    public String getLabel() {
        return label;
    }
    private void setLabel(String label) {
        this.label = label;
    }
    @JsonCreator
    public static TailoredScenario fromString(String value) {
        for (TailoredScenario scenario : TailoredScenario.values()) {
            if (scenario.name().replace("_", "-").equalsIgnoreCase(value) ||
                    scenario.name().replace("_", " ").equalsIgnoreCase(value)) {
                return scenario;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }

    @JsonValue
    public String toJson() {
        return this.name();
    }
}
