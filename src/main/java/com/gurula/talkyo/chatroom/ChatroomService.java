package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;

import java.util.Optional;

public interface ChatroomService {
    void save(Chatroom chatroom);

    String createChatroom(Member member);

    Optional<Chatroom> findById(String chatroomId);

}
