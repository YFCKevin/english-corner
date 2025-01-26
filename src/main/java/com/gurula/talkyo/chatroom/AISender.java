package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.SenderRole;

public class AISender implements MessageSender{
    private String translation;
    private final SenderRole senderRole = SenderRole.AI;
    @Override
    public SenderRole getSenderRole() {
        return this.senderRole;
    }

    public AISender(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}
