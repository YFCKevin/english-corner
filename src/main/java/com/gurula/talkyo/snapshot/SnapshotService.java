package com.gurula.talkyo.snapshot;

import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.snapshot.dto.SnapshotDTO;

import java.util.List;
import java.util.Optional;

public interface SnapshotService {
    SnapshotForm createLink(SnapshotDTO snapshotDTO, Member member);

    String updateLink(SnapshotDTO snapshotDTO);

    int deleteLink(String id);

    List<SnapshotForm> getAllLinks(String memberId);

    SnapshotForm info(String chatroomId, String memberId);

    SnapshotForm getInfoByLink(String link);
}
