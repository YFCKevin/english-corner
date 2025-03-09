package com.gurula.talkyo.snapshot;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SnapshotRepository extends MongoRepository<SnapshotForm, String> {
    int deleteSnapshotById(String id);

    List<SnapshotForm> findByMemberId(String memberId);

    Optional<SnapshotForm> findByChatroomIdAndMemberId(String chatroomId, String memberId);

    Optional<SnapshotForm> findByChatroomId(String chatroomId);

    Optional<SnapshotForm> findByLink(String link);
}
