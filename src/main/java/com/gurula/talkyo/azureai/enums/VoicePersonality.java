package com.gurula.talkyo.azureai.enums;

public enum VoicePersonality {
    CURIOUS(1, "好奇"),
    MATURE(2, "成熟"),
    SOOTHING(3, "舒緩"),
    WARM(4, "溫暖"),
    BRIGHT(5, "明亮"),
    STRONG(6, "強勁"),
    THOUGHTFUL(7, "深思"),
    EXPRESSIVE(8, "表現力"),
    UPBEAT(9, "愉快"),
    ENGAGING(10, "引人入勝"),
    CASUAL(11, "隨意"),
    SMOOTH(12, "流暢"),
    VERSATILE(13, "多才多藝"),
    POLITE(14, "禮貌"),
    CHEERFUL(15, "開朗"),
    AUTHORITATIVE(16, "權威"),
    SHY(17, "害羞"),
    PLEASANT(18, "愉快"),
    AUTHENTIC(19, "真誠"),
    RESONANT(20, "共鳴"),
    DEEP(21, "深沉"),
    APPROACHABLE(22, "親切"),
    FORMAL(23, "正式"),
    WHIMSICAL(24, "異想天開"),
    LIGHT_HEARTED(25, "輕鬆愉快"),
    PROFESSIONAL(26, "專業"),
    EMOTIONAL(27, "情感豐富"),
    YOUTHFUL(28, "年輕"),
    ANIMATED(29, "生動"),
    TRUSTWORTHY(30, "可信"),
    SINCERE(31, "真摯"),
    FRIENDLY(32, "友善"),
    CONFIDENT(33, "自信"),
    HOARSE(34, "沙啞"),
    CLEAR(35, "清晰"),
    CARING(36, "關懷"),
    HONEST(37, "誠實"),
    CALM(38, "冷靜"),
    CRISP(39, "清脆"),
    SERIOUS(40, "嚴肅"),
    WELL_ROUNDED(41, "全面"),
    EMPATHETIC(42, "有同理心");

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
}
