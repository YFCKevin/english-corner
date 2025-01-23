package com.gurula.talkyo.openai.dto;

import com.gurula.talkyo.openai.enums.Role;

public class MsgDTO {
    private Role role;
    private String content;

    public MsgDTO(Role role, String content) {
        this.role = role;
        this.content = content;
    }

    public Role getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
