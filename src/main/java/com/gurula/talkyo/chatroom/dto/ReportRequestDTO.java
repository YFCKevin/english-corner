package com.gurula.talkyo.chatroom.dto;

public class ReportRequestDTO {
    private String referenceText;
    private String destinationFilePath;
    private String dialogueText;

    public ReportRequestDTO() {
    }

    public ReportRequestDTO(String dialogueText) {
        this.dialogueText = dialogueText;
    }

    public ReportRequestDTO(String referenceText, String destinationFilePath) {
        this.referenceText = referenceText;
        this.destinationFilePath = destinationFilePath;
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

    @Override
    public String toString() {
        return "ReportRequestDTO{" +
                ", referenceText='" + referenceText + '\'' +
                ", destinationFilePath='" + destinationFilePath + '\'' +
                ", dialogueText='" + dialogueText + '\'' +
                '}';
    }
}
