package com.gurula.talkyo.chatroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.chatroom.dto.ChatDTO;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.chatroom.enums.SenderRole;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import com.gurula.talkyo.chatroom.utils.FileUtils;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.properties.ConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.gurula.talkyo.chatroom.ChatroomController.activeConversations;

@Service
public class ChatroomServiceImpl implements ChatroomService {
    private final ChatroomRepository chatroomRepository;
    private final SimpleDateFormat sdf;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ChatroomServiceImpl(ChatroomRepository chatroomRepository, @Qualifier("sdf") SimpleDateFormat sdf,
                               ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.chatroomRepository = chatroomRepository;
        this.sdf = sdf;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
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

    @Override
    public ConversationChainDTO reply(ChatDTO chatDTO, Member member) throws ExecutionException, InterruptedException, IOException {
        Message message = new Message();
        final String audioFileName = chatDTO.getAudioFileName();
        final String imageFileName = chatDTO.getImageFileName();
        final String content = chatDTO.getContent();    // 文字輸入
        if (StringUtils.isNotBlank(audioFileName)) {
            message.setMessageContents(List.of(new AudioContent(audioFileName, Files.size(Paths.get(audioFileName)))));
        } else if (StringUtils.isNotBlank(imageFileName)) {
            message.setMessageContents(List.of(new ImageContent(imageFileName, Files.size(Paths.get(imageFileName)))));
        }
        message.setSender(member.getId());
        message.setConversationId(chatDTO.getConversationId());
        message.setCreatedDateTime(sdf.format(new Date()));
        message.setSenderContent(new HumanSender());
        if (StringUtils.isNotBlank(content)) {
            message.getMessageContents().add(new TextContent(content));
        }
        final Message savedMessage = messageRepository.save(message);

        ConversationChainDTO conversationChainDTO = null;
        if (activeConversations.containsKey(chatDTO.getConversationId())) {
            final AbstractConversation conversation = activeConversations.get(chatDTO.getConversationId());
            conversation.setChatRequestDTO(new ChatRequestDTO(member.getId(), member.getPartnerId(), savedMessage.getId(), chatDTO.getLessonId()));
            conversationChainDTO = conversation.chatting();
        }
        return conversationChainDTO;
    }

    @Override
    public ConversationChainDTO end(ChatDTO chatDTO) throws IOException, ExecutionException, InterruptedException {

        final String conversationId = chatDTO.getConversationId();

        if (activeConversations.containsKey(conversationId)) {
            final AbstractConversation activeConversation = activeConversations.get(conversationId);
            activeConversation.setChatRequestDTO(new ChatRequestDTO(conversationId, chatDTO.getLessonId()));
            return activeConversation.finish();
        }

        return null;
    }
}
