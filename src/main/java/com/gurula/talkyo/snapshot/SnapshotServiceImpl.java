package com.gurula.talkyo.snapshot;

import cn.hutool.core.lang.Opt;
import com.gurula.talkyo.chatroom.Chatroom;
import com.gurula.talkyo.chatroom.ChatroomRepository;
import com.gurula.talkyo.chatroom.Message;
import com.gurula.talkyo.chatroom.MessageRepository;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.snapshot.dto.SnapshotDTO;
import com.gurula.talkyo.snapshot.enums.SnapshotType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SnapshotServiceImpl implements SnapshotService{
    private final SnapshotRepository snapshotRepository;
    private final MessageRepository messageRepository;
    private final ChatroomRepository chatroomRepository;
    private final SimpleDateFormat sdf;

    public SnapshotServiceImpl(SnapshotRepository snapshotRepository, MessageRepository messageRepository,
                               ChatroomRepository chatroomRepository, @Qualifier("sdf") SimpleDateFormat sdf) {
        this.snapshotRepository = snapshotRepository;
        this.messageRepository = messageRepository;
        this.chatroomRepository = chatroomRepository;
        this.sdf = sdf;
    }

    @Transactional
    @Override
    public SnapshotForm createLink(SnapshotDTO snapshotDTO, Member member) {
        final String chatroomId = snapshotDTO.getChatroomId();
        final List<SnapshotDetail> snapshotDetails = getLatestMessages(chatroomId);

        final Chatroom chatroom = chatroomRepository.findById(chatroomId).get();

        SnapshotForm snapshotForm = new SnapshotForm(
                chatroomId,
                StringUtils.isNotBlank(chatroom.getTitle()) ? chatroom.getTitle() : "未命名",
                sdf.format(new Date()),
                member.getId(),
                snapshotDetails,
                SnapshotType.CHAT
        );

        return snapshotRepository.save(snapshotForm);
    }


    @Override
    public String updateLink(SnapshotDTO snapshotDTO) {
        final String chatroomId = snapshotDTO.getChatroomId();
        final SnapshotForm snapshotForm = snapshotRepository.findByChatroomId(chatroomId).get();
        final Chatroom chatroom = chatroomRepository.findById(chatroomId).get();
        final List<SnapshotDetail> snapshotDetails = getLatestMessages(chatroomId);
        snapshotForm.setSnapshotDetails(snapshotDetails);
        snapshotForm.setTitle(StringUtils.isNotBlank(chatroom.getTitle()) ? chatroom.getTitle() : "未命名");
        snapshotForm.setModificationDateTime(sdf.format(new Date()));
        final SnapshotForm savedSnapshotForm = snapshotRepository.save(snapshotForm);
        if (StringUtils.isNotBlank(savedSnapshotForm.getModificationDateTime())) {
            return savedSnapshotForm.getLink();
        } else {
            return null;
        }
    }

    @Override
    public int deleteLink(String id) {
        return snapshotRepository.deleteSnapshotById(id);
    }

    @Override
    public List<SnapshotForm> getAllLinks(String memberId) {
        return snapshotRepository.findByMemberId(memberId);
    }

    @Override
    public SnapshotForm info(String chatroomId, String memberId) {
        final Optional<SnapshotForm> opt = snapshotRepository.findByChatroomIdAndMemberId(chatroomId, memberId);
        return opt.orElseGet(SnapshotForm::new);
    }

    @Override
    public SnapshotForm getInfoByLink(String link) {
        final Optional<SnapshotForm> opt = snapshotRepository.findByLink(link);
        return opt.orElseGet(SnapshotForm::new);
    }


    private List<SnapshotDetail> getLatestMessages(String chatroomId) {
        final List<Message> messages = messageRepository.findAllByChatroomIdOrderByCreatedDateTimeAsc(chatroomId);
        return messages.stream()
                .map(message -> {
                    SnapshotDetail snapshotDetail = new SnapshotDetail();
                    if (StringUtils.isNotBlank(message.getAudioName())) {
                        snapshotDetail.setAudioName(message.getAudioName());
                    }
                    if (StringUtils.isNotBlank(message.getParsedText())) {
                        snapshotDetail.setParsedText(message.getParsedText());
                    }
                    if (StringUtils.isNotBlank(message.getText())) {
                        snapshotDetail.setText(message.getText());
                    }
                    if (StringUtils.isNotBlank(message.getImageName())) {
                        snapshotDetail.setImageName(message.getImageName());
                    }
                    if (StringUtils.isNotBlank(message.getTranslation())) {
                        snapshotDetail.setTranslation(message.getTranslation());
                    }
                    snapshotDetail.setSenderRole(message.getSenderRole());
                    snapshotDetail.setMessageId(message.getId());
                    return snapshotDetail;
                }).toList();
    }
}
