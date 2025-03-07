package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.enums.ChatroomType;
import com.gurula.talkyo.chatroom.enums.RoomStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatroomRepository extends MongoRepository<Chatroom, String> {

    List<Chatroom> findByOwnerIdAndChatroomTypeAndRoomStatusOrderByCreationDateDesc(String memberId, ChatroomType chatroomType, RoomStatus roomStatus);

    List<Chatroom> findByOwnerIdAndChatroomTypeAndRoomStatusOrderByCloseDateAsc(String memberId, ChatroomType chatroomType, RoomStatus roomStatus);

    List<Chatroom> findByOwnerIdAndRoomStatusAndChatroomTypeInOrderByCloseDateAsc(String memberId, RoomStatus roomStatus, List<ChatroomType> project);

    List<Chatroom> findByOwnerIdAndChatroomTypeOrderByCloseDateAsc(String memberId, ChatroomType chatroomType);

    List<Chatroom> findByOwnerIdAndChatroomType(String memberId, ChatroomType chatroomType);

    int deleteChatroomById(String chatroomId);
}
