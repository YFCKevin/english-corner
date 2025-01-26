package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.enums.ConversationType;
import com.gurula.talkyo.chatroom.enums.MessageType;
import org.springframework.web.multipart.MultipartFile;

public class ChatDTO {
    private ConversationType conversationType;
    private String chatroomId;
    private String conversationId;
    private String content;
    private MultipartFile multipartFile;
    private String audioFileName;
    private String imageFileName;
    private String lessonId;
    private MessageType messageType;

    public String getConversationId() {
        return conversationId;
    }

    public String getContent() {
        return content;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public String getImageFileName() {
        return imageFileName;
    }
}
