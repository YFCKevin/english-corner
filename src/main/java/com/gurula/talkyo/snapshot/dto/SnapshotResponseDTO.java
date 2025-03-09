package com.gurula.talkyo.snapshot.dto;

import com.gurula.talkyo.snapshot.SnapshotDetail;
import com.gurula.talkyo.snapshot.enums.SnapshotType;

import java.util.ArrayList;
import java.util.List;

public class SnapshotResponseDTO {
    private String id;
    private String title;
    private String sharingDateTime;
    private List<SnapshotDetail> snapshotDetails = new ArrayList<>();
    private String snapshotTypeLabel;
    private String link;

    public SnapshotResponseDTO() {
    }

    public SnapshotResponseDTO(String id, String title, String sharingDateTime, String snapshotTypeLabel, String link) {
        this.id = id;
        this.title = title;
        this.sharingDateTime = sharingDateTime;
        this.snapshotTypeLabel = snapshotTypeLabel;
        this.link = link;
    }

    public String getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public String getSharingDateTime() {
        return sharingDateTime;
    }

    public List<SnapshotDetail> getSnapshotDetails() {
        return snapshotDetails;
    }

    public String getSnapshotTypeLabel() {
        return snapshotTypeLabel;
    }

    public String getLink() {
        return link;
    }
}
