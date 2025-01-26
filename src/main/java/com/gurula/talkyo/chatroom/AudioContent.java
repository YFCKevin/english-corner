package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.MessageType;

public class AudioContent implements MessageContent{
    private final String audioName;
    private String parsedText;
    private final long size;
    @Override
    public MessageType getType() {
        return MessageType.AUDIO;
    }

    public AudioContent(String audioName, long size) {
        this.audioName = audioName;
        this.size = size;
    }

    public AudioContent(String audioName, long size, String parsedText) {
        this.audioName = audioName;
        this.size = size;
        this.parsedText = parsedText;
    }

    public String getAudioName() {
        return audioName;
    }

    public long getSize() {
        return size;
    }

    public String getParsedText() {
        return parsedText;
    }
}
