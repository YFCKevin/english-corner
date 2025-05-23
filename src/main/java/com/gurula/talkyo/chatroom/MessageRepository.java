package com.gurula.talkyo.chatroom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findAllByChatroomIdOrderByCreatedDateTimeAsc(String chatroomId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'conversationScore': ?1 } }")
    void updateConversationScore(String messageId, ConversationScore conversationScore);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'grammarResult': ?1 } }")
    void updateGrammarResult(String messageId, GrammarResult grammarResult);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'advancedSentences': ?1 } }")
    void updateAdvancedSentences(String messageId, List<AdvancedSentence> sentences);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'accuracy': ?1 } }")
    void updateAccuracy(String messageId, boolean accuracy);

    // 用於確認聊天室 Chatroom 是否新舊
    boolean existsByChatroomId(String chatroomId);

    Optional<Message> findFirstByChatroomIdAndCreatedDateTimeLessThanOrderByCreatedDateTimeDesc(String chatroomId, String createdDateTime);

    List<Message> findByPreviewMessageId(String previewMessageId);

    Optional<Message> findFirstByBranchOrderByCreatedDateTimeDesc(String branch);

    List<Message> findBySenderAndAudioNameIsNotNull(String memberId);

    Optional<Message> findByPreviewMessageIdAndVersion(String previewMessageId, int targetVersion);

    Optional<Message> findByChatroomIdAndCurrentLastMsg(String chatroomId, boolean currentLastMsg);

    @Query("{'id' : {$in : ?0}}")
    int deleteMessageById(List<String> messageIds);

    List<Message> findByIdIn(List<String> messageIds);
}
