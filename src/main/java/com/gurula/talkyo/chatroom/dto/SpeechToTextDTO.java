package com.gurula.talkyo.chatroom.dto;

public class SpeechToTextDTO {
    private String chatroomId;
    private String text;
    private String audioName;

    public SpeechToTextDTO() {
    }

    public SpeechToTextDTO(String chatroomId, String text, String audioName) {
        this.chatroomId = chatroomId;
        this.text = text;
        this.audioName = audioName;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public String getText() {
        return text;
    }

    public String getAudioName() {
        return audioName;
    }
}
