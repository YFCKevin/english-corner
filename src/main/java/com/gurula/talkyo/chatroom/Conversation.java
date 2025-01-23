package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.ConversationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "conversation")
public class Conversation {
    @Id
    private String id;
    private String chatroomId;
    private Scenario scenario;
    private ConversationType conversationType;
    private String startedDateTime;
    private String finishedDateTime;

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

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public String getStartedDateTime() {
        return startedDateTime;
    }

    public void setStartedDateTime(String startedDateTime) {
        this.startedDateTime = startedDateTime;
    }

    public String getFinishedDateTime() {
        return finishedDateTime;
    }

    public void setFinishedDateTime(String finishedDateTime) {
        this.finishedDateTime = finishedDateTime;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }
}
