package com.gurula.talkyo.record;

import com.gurula.talkyo.record.dto.RecordDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LearningRecordServiceImpl implements LearningRecordService{
    private final LearningRecordRepository learningRecordRepository;

    public LearningRecordServiceImpl(LearningRecordRepository learningRecordRepository) {
        this.learningRecordRepository = learningRecordRepository;
    }

    @Override
    public int saveRecord(RecordDTO recordDTO, String memberId) {
        LearningRecord record = new LearningRecord(recordDTO, false, memberId);
        LearningRecord savedRecord = learningRecordRepository.save(record);
        return savedRecord != null ? 1 : 0;
    }

    @Override
    public int finish(String lessonId) {
        Optional<LearningRecord> opt = learningRecordRepository.findByLessonId(lessonId);
        if (opt.isEmpty()) {
            // 開始課程紀錄不存在
            return 0;
        } else {
            final LearningRecord record = opt.get();
            LearningRecord updateRecord = new LearningRecord(record, true);
            final LearningRecord savedRecord = learningRecordRepository.save(updateRecord);
            return savedRecord != null ? 1 : 0;
        }
    }
}
