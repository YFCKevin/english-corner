package com.gurula.talkyo.record;

import com.gurula.talkyo.record.dto.RecordDTO;

public interface LearningRecordService {
    int saveRecord(RecordDTO recordDTO, String memberId);

    int finish(String lessonId);
}
