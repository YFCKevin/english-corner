package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.MessageType;

public class TextContent implements MessageContent{
    private String text;
    @Override
    public MessageType getType() {
        return MessageType.TEXT;
    }

    public TextContent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
