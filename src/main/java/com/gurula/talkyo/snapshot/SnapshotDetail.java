package com.gurula.talkyo.snapshot;

import com.gurula.talkyo.chatroom.enums.SenderRole;

public class SnapshotDetail {
    private String messageId;
    private SenderRole senderRole;
    // partner
    private String translation;
    // text
    private String text;
    // image
    private String imageName;
    // audio
    private String audioName;
    private String parsedText;

    public SnapshotDetail() {
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }

    public void setSenderRole(SenderRole senderRole) {
        this.senderRole = senderRole;
    }

    public SenderRole getSenderRole() {
        return senderRole;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTranslation() {
        return translation;
    }

    public String getText() {
        return text;
    }

    public String getImageName() {
        return imageName;
    }

    public String getAudioName() {
        return audioName;
    }

    public String getParsedText() {
        return parsedText;
    }
}
