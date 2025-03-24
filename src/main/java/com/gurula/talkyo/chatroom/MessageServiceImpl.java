package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.chatroom.dto.ChatDTO;
import com.gurula.talkyo.chatroom.enums.MessageType;
import com.gurula.talkyo.chatroom.handler.MessageTypeHandler;
import com.gurula.talkyo.course.Sentence;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberRepository;
import com.gurula.talkyo.properties.ConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MongoTemplate mongoTemplate;
    private final MemberRepository memberRepository;
    private final MessageTypeHandler handler;
    private final ConfigProperties configProperties;

    public MessageServiceImpl(MessageRepository messageRepository, MongoTemplate mongoTemplate,
                              MemberRepository memberRepository, MessageTypeHandler handler, ConfigProperties configProperties) {
        this.messageRepository = messageRepository;
        this.mongoTemplate = mongoTemplate;
        this.memberRepository = memberRepository;
        this.handler = handler;
        this.configProperties = configProperties;
    }

    @Override
    public List<Map<Integer, Message>> getHistoryMessageWhenSwitchBranch(String previewMessageId, int targetVersion) {

        List<Map<Integer, Message>> historyMsgs = new ArrayList<>();

        final Optional<Message> opt = messageRepository.findByPreviewMessageIdAndVersion(previewMessageId, targetVersion);
        if (opt.isPresent()) {
            final Message message = opt.get();
            final Message lastMessage = messageRepository.findFirstByBranchOrderByCreatedDateTimeDesc(message.getBranch()).get();
            final List<Message> messages = messageRepository.findAllByChatroomIdOrderByCreatedDateTimeAsc(lastMessage.getChatroomId())
                    .stream()
                    .peek(msg -> msg.setCurrentLastMsg(false))
                    .toList();
            messages.stream()
                    .filter(msg -> msg.getId().equals(lastMessage.getId()))
                    .findFirst()
                    .ifPresent(msg -> msg.setCurrentLastMsg(true));
            messageRepository.saveAll(messages);

            historyMsgs = getHistoryMessages(lastMessage.getId());
        }

        return historyMsgs;
    }

    @Override
    public List<Map<Integer, Message>> getHistoryMessages(String currentMessageId) {
        List<Map<Integer, Message>> historyMsgs = new ArrayList<>();
        String currentId = currentMessageId;

        while (StringUtils.isNotBlank(currentId)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(currentId));
            final Message currentMessage = mongoTemplate.findOne(query, Message.class);

            if (currentMessage != null) {
                final int maxVersion = messageRepository.findByPreviewMessageId(currentMessage.getPreviewMessageId()).stream()
                        .mapToInt(Message::getVersion)
                        .max().orElse(1);
                historyMsgs.add(Map.of(maxVersion, currentMessage));    // UI顯示： [currentMessage.getVersion() / maxVersion]
                currentId = currentMessage.getPreviewMessageId();
            } else {
                break;
            }
        }

        Collections.reverse(historyMsgs);
        return historyMsgs;
    }

    @Override
    public void saveAudio(Member member, String unitNumber, String fileName) {
        final List<Sentence> savedFavoriteSentences = member.getSavedFavoriteSentences();
        savedFavoriteSentences.stream()
                .filter(sentence -> unitNumber.equals(sentence.getUnitNumber()))
                .findFirst()
                .ifPresent(sentence -> sentence.setAudioName(Collections.singletonList(fileName)));

        memberRepository.save(member);
    }

    @Override
    public void deleteMessage(String[] msgIds) throws IOException {
        final List<String> messageIds = Arrays.stream(msgIds).toList();
        List<Message> messages = messageRepository.findByIdIn(messageIds);

        messageRepository.deleteAll(messages);

        for (Message message : messages) {
            ChatDTO chatDTO = new ChatDTO();
            chatDTO.setChatroomId(message.getChatroomId());
            if (StringUtils.isNotBlank(message.getAudioName())) {
                chatDTO.setMessageType(MessageType.AUDIO);
                chatDTO.setAudioFileName(message.getAudioName());
            } else if (StringUtils.isNotBlank(message.getImageName())) {
                chatDTO.setMessageType(MessageType.IMAGE);
            } else {
                chatDTO.setMessageType(MessageType.TEXT);
            }
            handler.deleteFile(chatDTO, configProperties);
        }
    }
}
