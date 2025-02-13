package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.enums.ScenarioCategory;

public class ScenarioDTO {
    private String unitNumber;
    private String coverName;
    private ScenarioCategory category;
    private String zhTitle;
    private String enTitle;
    private String partnerRole; // AI的角色
    private String humanRole;   // 我的角色
    private String subject;   // 話題or情境

    public ScenarioDTO() {
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public String getCoverName() {
        return coverName;
    }

    public ScenarioCategory getCategory() {
        return category;
    }

    public String getZhTitle() {
        return zhTitle;
    }

    public String getEnTitle() {
        return enTitle;
    }

    public String getPartnerRole() {
        return partnerRole;
    }

    public String getHumanRole() {
        return humanRole;
    }

    public String getSubject() {
        return subject;
    }
}
