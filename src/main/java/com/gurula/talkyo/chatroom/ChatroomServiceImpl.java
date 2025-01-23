package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
public class ChatroomServiceImpl implements ChatroomService{
    private final ChatroomRepository chatroomRepository;
    private final SimpleDateFormat sdf;
    private final ConversationRepository conversationRepository;

    public ChatroomServiceImpl(ChatroomRepository chatroomRepository, @Qualifier("sdf") SimpleDateFormat sdf,
                               ConversationRepository conversationRepository) {
        this.chatroomRepository = chatroomRepository;
        this.sdf = sdf;
        this.conversationRepository = conversationRepository;
    }

    @Override
    public void save(Chatroom chatroom) {
        chatroomRepository.save(chatroom);
    }

    @Override
    public String createChatroom(Member member) {
        Optional<Chatroom> opt = chatroomRepository.findByOwnerIdOrParticipantsContainingBoth(member.getId(), member.getPartnerId());
        if (opt.isEmpty()) {
            Chatroom chatroom = new Chatroom();
            chatroom.setParticipants(Arrays.asList(member.getId(), member.getPartnerId()));
            chatroom.setCreationDate(sdf.format(new Date()));
            chatroom.setOwnerId(member.getId());
            final Chatroom savedChatroom = chatroomRepository.save(chatroom);
            return savedChatroom.getId();
        } else {
            final Chatroom chatroom = opt.get();
            return chatroom.getId();
        }
    }

    @Override
    public Optional<Chatroom> findById(String chatroomId) {
        return chatroomRepository.findById(chatroomId);
    }

}
