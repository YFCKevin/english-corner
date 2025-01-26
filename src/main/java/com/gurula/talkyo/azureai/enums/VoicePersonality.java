package com.gurula.talkyo.azureai.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VoicePersonality {
    CURIOUS(1, "好奇"),
    MATURE(2, "成熟"),
    SOOTHING(3, "舒緩"),
    WARM(4, "溫暖"),
    STRONG(5, "強大"),
    THOUGHTFUL(6, "深思熟慮"),
    EXPRESSIVE(7, "富有表現力"),
    UPBEAT(8, "愉快"),
    ENGAGING(9, "吸引人"),
    CASUAL(10, "隨意"),
    SMOOTH(11, "順暢"),
    VERSATILE(12, "多才多藝"),
    POLITE(13, "有禮"),
    CHEERFUL(14, "愉快"),
    AUTHORITATIVE(15, "權威"),
    SHY(16, "害羞"),
    PLEASANT(17, "愉快"),
    RESONANT(18, "共鳴"),
    AUTHENTIC(19, "真實"),
    DEEP(20, "深沉"),
    APPROACHABLE(21, "平易近人"),
    FORMAL(22, "正式"),
    WHIMSICAL(23, "異想天開"),
    LIGHT_HEARTED(24, "輕鬆"),
    PROFESSIONAL(25, "專業"),
    EMOTIONAL(26, "感性"),
    YOUTHFUL(27, "年輕"),
    ANIMATED(28, "生動"),
    TRUSTHWORTHY(29, "值得信賴"),
    SINCERE(30, "真誠"),
    FRIENDLY(31, "友善"),
    CONFIDENT(32, "自信"),
    BRIGHT(33, "明亮"),
    HOARSE(34, "沙啞"),
    CLEAR(35, "清晰"),
    CARING(36, "關懷"),
    HONEST(37, "誠實"),
    GENTLE(38, "溫和"),
    KNOWLEDGABLE(39, "博學"),
    CALM(40, "冷靜"),
    SOFT(41, "柔和"),
    CRISP(42, "脆"),
    SERIOUS(43, "嚴肅"),
    WELL_ROUNDED(44, "全面"),
    EMPATHETIC(45, "富有同理心");
    private int value;
    private String label;
    private VoicePersonality(int value,String label){
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
    public static VoicePersonality fromString(String value) {
        for (VoicePersonality personality : VoicePersonality.values()) {
            if (personality.name().replace("_", "-").equalsIgnoreCase(value)) {
                return personality;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }

    @JsonValue
    public String toJson() {
        return this.name();
    }
}
