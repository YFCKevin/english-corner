package com.gurula.talkyo.snapshot;

import cn.hutool.core.collection.AvgPartition;
import com.gurula.talkyo.snapshot.enums.SnapshotType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "snapshot")
public class SnapshotForm {
    @Id
    private String id;
    private String chatroomId;
    private String title;
    private String creationDateTime;
    private String modificationDateTime;
    private String memberId;
    private List<SnapshotDetail> snapshotDetails = new ArrayList<>();
    private SnapshotType snapshotType;
    private String link;

    public SnapshotForm(String chatroomId, String title, String creationDateTime, String memberId, List<SnapshotDetail> snapshotDetails, SnapshotType snapshotType) {
        this.chatroomId = chatroomId;
        this.title = title;
        this.creationDateTime = creationDateTime;
        this.memberId = memberId;
        this.snapshotDetails = snapshotDetails;
        this.snapshotType = snapshotType;
        this.link = genLink();
    }

    public SnapshotForm() {
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public String getMemberId() {
        return memberId;
    }

    public List<SnapshotDetail> getSnapshotDetails() {
        return snapshotDetails;
    }

    public SnapshotType getSnapshotType() {
        return snapshotType;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public String getLink() {
        return link;
    }

    public String getModificationDateTime() {
        return modificationDateTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setModificationDateTime(String modificationDateTime) {
        this.modificationDateTime = modificationDateTime;
    }

    public void setSnapshotDetails(List<SnapshotDetail> snapshotDetails) {
        this.snapshotDetails = snapshotDetails;
    }

    public String genLink() {
        return UUID.randomUUID().toString();
    }

}
