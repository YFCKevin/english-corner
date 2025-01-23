package com.gurula.talkyo.chatroom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ChatroomRepository extends MongoRepository<Chatroom, String> {
    @Query("{ $or: [ { 'ownerId': ?0 }, { 'participants': { $all: [?0, ?1] } } ] }")
    Optional<Chatroom> findByOwnerIdOrParticipantsContainingBoth(String memberId, String partnerId);
}
