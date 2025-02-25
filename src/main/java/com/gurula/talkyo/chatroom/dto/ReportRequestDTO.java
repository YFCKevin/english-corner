package com.gurula.talkyo.chatroom.dto;

public class ReportRequestDTO {
    private String referenceText;
    private String destinationFilePath;
    private String dialogueText;

    public ReportRequestDTO() {
    }

    public ReportRequestDTO(String referenceText, String dialogueText) {
        this.referenceText = referenceText;
        this.dialogueText = dialogueText;
    }

    public ReportRequestDTO(String referenceText, String destinationFilePath, String dialogueText) {
        this.referenceText = referenceText;
        this.destinationFilePath = destinationFilePath;
        this.dialogueText = dialogueText;
    }

    public String getReferenceText() {
        return referenceText;
    }

    public String getDestinationFilePath() {
        return destinationFilePath;
    }

    public String getDialogueText() {
        return dialogueText;
    }

    public void setDestinationFilePath(String destinationFilePath) {
        this.destinationFilePath = destinationFilePath;
    }

    @Override
    public String toString() {
        return "ReportRequestDTO{" +
                ", referenceText='" + referenceText + '\'' +
                ", destinationFilePath='" + destinationFilePath + '\'' +
                ", dialogueText='" + dialogueText + '\'' +
                '}';
    }
}
