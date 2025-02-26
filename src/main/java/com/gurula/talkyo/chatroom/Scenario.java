package com.gurula.talkyo.chatroom;

public class Scenario {
    private String unitNumber;
    private String partnerRole; // AI的角色
    private String humanRole;   // 我的角色
    private String subject;   // 話題or情境

    public Scenario() {
    }

    // for SITUATION
    public Scenario(String unitNumber, String humanRole, String partnerRole, String subject) {
        this.unitNumber = unitNumber;
        this.partnerRole = partnerRole;
        this.humanRole = humanRole;
        this.subject = subject;
    }

    // for PROJECT
    public Scenario(String humanRole, String partnerRole, String subject) {
        this.partnerRole = partnerRole;
        this.humanRole = humanRole;
        this.subject = subject;
    }

    public String getPartnerRole() {
        return partnerRole;
    }

    public void setPartnerRole(String partnerRole) {
        this.partnerRole = partnerRole;
    }

    public String getHumanRole() {
        return humanRole;
    }

    public void setHumanRole(String humanRole) {
        this.humanRole = humanRole;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "partnerRole='" + partnerRole + '\'' +
                ", humanRole='" + humanRole + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
