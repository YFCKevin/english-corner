package com.gurula.talkyo.chatroom;

import com.gurula.talkyo.course.Sentence;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService{
    private final MessageRepository messageRepository;
    private final MongoTemplate mongoTemplate;
    private final MemberRepository memberRepository;

    public MessageServiceImpl(MessageRepository messageRepository, MongoTemplate mongoTemplate,
                              MemberRepository memberRepository) {
        this.messageRepository = messageRepository;
        this.mongoTemplate = mongoTemplate;
        this.memberRepository = memberRepository;
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
}
