package com.gurula.talkyo.openai.dto;

import java.util.List;

public class PayloadDTO {
    private String model;
    private List<MsgDTO> messages;
    private float temperature;
    private int token;

    public PayloadDTO(String model, List<MsgDTO> messages, float temperature, int token) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.token = token;
    }

    public PayloadDTO(String model, List<MsgDTO> messages, float temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public List<MsgDTO> getMessages() {
        return messages;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getToken() {
        return token;
    }
}
