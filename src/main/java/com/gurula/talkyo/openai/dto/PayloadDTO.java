package com.gurula.talkyo.openai.dto;

import java.util.List;

public class PayloadDTO {
    private String model;
    private List<MsgDTO> messages;

    public PayloadDTO(String model, List<MsgDTO> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public List<MsgDTO> getMessages() {
        return messages;
    }
}
