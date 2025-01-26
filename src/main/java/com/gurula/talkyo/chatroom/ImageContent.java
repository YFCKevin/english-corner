package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.MessageType;

public class ImageContent implements MessageContent{
    private String imageName;
    private long size;
    @Override
    public MessageType getType() {
        return MessageType.IMAGE;
    }

    public ImageContent(String imageName, long size) {
        this.imageName = imageName;
        this.size = size;
    }
}
