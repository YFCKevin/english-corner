package com.gurula.talkyo.chatroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.chatroom.dto.ChatDTO;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface ChatroomService {
    void save(Chatroom chatroom);

    String createChatroom(Member member);

    Optional<Chatroom> findById(String chatroomId);

    ConversationChainDTO reply(ChatDTO chatDTO, Member member) throws ExecutionException, InterruptedException, IOException;

    ConversationChainDTO end(ChatDTO chatDTO) throws IOException, ExecutionException, InterruptedException;
}
