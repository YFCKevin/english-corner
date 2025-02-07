package com.gurula.talkyo.chatroom;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService{
    private final MessageRepository messageRepository;
    private final MongoTemplate mongoTemplate;

    public MessageServiceImpl(MessageRepository messageRepository, MongoTemplate mongoTemplate) {
        this.messageRepository = messageRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Map<Integer, Message>> getHistoryMessageWhenSwitchBranch(String branch) {

        List<Map<Integer, Message>> historyMsgs = new ArrayList<>();

        final Optional<Message> opt = messageRepository.findFirstByBranchOrderByCreatedDateTimeDesc(branch);
        if (opt.isPresent()) {
            final Message message = opt.get();
             historyMsgs = getHistoryMessages(message.getId());
        }

        return historyMsgs;
    }

    @Override
    public List<Map<Integer, Message>> getHistoryMessages(String currentMessageId) {
        List<Map<Integer, Message>> historyMsgs = new ArrayList<>();
        String currentId = currentMessageId;

        while (currentId != null) {
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
}
