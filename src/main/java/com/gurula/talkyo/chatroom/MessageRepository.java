package com.gurula.talkyo.chatroom;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByConversationIdOrderByCreatedDateTimeAsc(String conversationId);
}
