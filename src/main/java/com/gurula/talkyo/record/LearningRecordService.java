package com.gurula.talkyo.record;

import com.gurula.talkyo.record.dto.RecordDTO;

import java.util.Optional;

public interface LearningRecordService {
    int saveRecord(RecordDTO recordDTO, String memberId);

    int finish(String chatroomId);

}
