package com.gurula.talkyo.chatroom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;


public interface ConversationRepository extends MongoRepository<Conversation, String> {

    @Query("{ 'chatroomId': ?0, 'memberId': ?1 }")
    @Update("{ '$set': { 'leftDateTime': ?2 } }")
    void leaveChatroom(String chatroomId, String participantId, String today);
}
