package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.ChatroomType;
import com.gurula.talkyo.chatroom.enums.RoomStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatroom")
public class Chatroom {
    @Id
    private String id;
    private RoomStatus roomStatus;  // active, close
    private ChatroomType chatroomType;
    private String creationDate;
    private String closeDate;
    private String ownerId; // memberId
    private Scenario scenario;
    private LearningReport report;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public ChatroomType getChatroomType() {
        return chatroomType;
    }

    public void setChatroomType(ChatroomType chatroomType) {
        this.chatroomType = chatroomType;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public LearningReport getReport() {
        return report;
    }

    public void setReport(LearningReport report) {
        this.report = report;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
