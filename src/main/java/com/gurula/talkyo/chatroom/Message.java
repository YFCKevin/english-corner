package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.chatroom.enums.SenderRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "message")
public class Message {
    @Id
    private String id;
    private List<MessageContent> messageContents = new ArrayList<>();
    private String conversationId;
    private String sender;  // memberId or partnerId
    private MessageSender senderContent;
    private String createdDateTime;
    private String updatedDateTime;
    private boolean accuracy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public List<MessageContent> getMessageContents() {
        return messageContents;
    }

    public void setMessageContents(List<MessageContent> messageContents) {
        this.messageContents = messageContents;
    }

    public MessageSender getSenderContent() {
        return senderContent;
    }

    public void setSenderContent(MessageSender senderContent) {
        this.senderContent = senderContent;
    }

    public boolean isAccuracy() {
        return accuracy;
    }

    public void setAccuracy(boolean accuracy) {
        this.accuracy = accuracy;
    }
}
