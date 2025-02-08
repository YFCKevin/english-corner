package com.gurula.talkyo.chatroom.dto;

import com.gurula.talkyo.chatroom.enums.ActionType;
import com.gurula.talkyo.chatroom.enums.ChatroomType;
import com.gurula.talkyo.chatroom.enums.MessageType;
import org.springframework.web.multipart.MultipartFile;

public class ChatDTO {
    private ChatroomType chatroomType;
    private String chatroomId;
    private MultipartFile multipartFile;
    private String content;
    private String audioFileName;
    private String imageFileName;
    private String lessonId;
    private MessageType messageType;
    private String previewMessageId;
    private int branch;
    private ActionType action; // 在 FREE_TALK 用來辨識是新增訊息 or 修改訊息
    private String messageId;   // human msg id 傳遞給 reply 方法

    public String getContent() {
        return content;
    }

    public ChatroomType getChatroomType() {
        return chatroomType;
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

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setChatroomType(ChatroomType chatroomType) {
        this.chatroomType = chatroomType;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getPreviewMessageId() {
        return previewMessageId;
    }

    public void setPreviewMessageId(String previewMessageId) {
        this.previewMessageId = previewMessageId;
    }

    public int getBranch() {
        return branch;
    }

    public void setBranch(int branch) {
        this.branch = branch;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "ChatDTO{" +
                "chatroomType=" + chatroomType +
                ", chatroomId='" + chatroomId + '\'' +
                ", multipartFile=" + multipartFile +
                ", content='" + content + '\'' +
                ", audioFileName='" + audioFileName + '\'' +
                ", imageFileName='" + imageFileName + '\'' +
                ", lessonId='" + lessonId + '\'' +
                ", messageType=" + messageType +
                ", previewMessageId='" + previewMessageId + '\'' +
                ", branch=" + branch +
                ", action=" + action +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
