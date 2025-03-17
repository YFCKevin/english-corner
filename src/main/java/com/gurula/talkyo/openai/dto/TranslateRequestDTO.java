package com.gurula.talkyo.openai.dto;

public class TranslateRequestDTO {
    private String text;    // 英文句子
    private String unitNumber;  // for 收藏時的句子翻譯

    public String getText() {
        return text;
    }

    public String getUnitNumber() {
        return unitNumber;
    }
}
