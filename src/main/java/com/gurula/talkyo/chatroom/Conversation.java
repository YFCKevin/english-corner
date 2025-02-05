package com.gurula.talkyo.chatroom;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * member and partner 參與聊天室的對話紀錄
 */
@Document(collection = "conversation")
public class Conversation {
    @Id
    private String id;
    private String chatroomId;
    private String memberId;
    private String joinDateTime;
    private String leftDateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getJoinDateTime() {
        return joinDateTime;
    }

    public void setJoinDateTime(String joinDateTime) {
        this.joinDateTime = joinDateTime;
    }

    public String getLeftDateTime() {
        return leftDateTime;
    }

    public void setLeftDateTime(String leftDateTime) {
        this.leftDateTime = leftDateTime;
    }

    public Conversation enterChatroom(String participantId, String chatroomId) {
        this.chatroomId = chatroomId;
        this.joinDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.memberId = participantId;
        return this;
    }
}
