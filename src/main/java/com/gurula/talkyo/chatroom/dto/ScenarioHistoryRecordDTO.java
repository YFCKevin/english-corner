package com.gurula.talkyo.chatroom.dto;

public class ScenarioHistoryRecordDTO {
    private String chatroomId;
    private String unitNumber;
    private String closeDate;
    private double overallRating;

    public ScenarioHistoryRecordDTO() {
    }

    public ScenarioHistoryRecordDTO(String chatroomId, String unitNumber, String closeDate, double overallRating) {
        this.chatroomId = chatroomId;
        this.unitNumber = unitNumber;
        this.closeDate = closeDate;
        this.overallRating = overallRating;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public double getOverallRating() {
        return overallRating;
    }
}
