package com.gurula.talkyo.record;

import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberContext;
import com.gurula.talkyo.record.dto.RecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/record")
public class LearningRecordController {
    private final Logger logger = LoggerFactory.getLogger(LearningRecordController.class);
    private final LearningRecordService learningRecordService;

    public LearningRecordController(LearningRecordService learningRecordService) {
        this.learningRecordService = learningRecordService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> save (@RequestBody RecordDTO recordDTO){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [learning record save]", member.getName(), member.getId());

        ResultStatus resultStatus = new ResultStatus();

        int count = learningRecordService.saveRecord(recordDTO, member.getId());
        if (count > 0) {
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
        } else {
            resultStatus.setCode("C999");
            resultStatus.setMessage("儲存失敗");
        }
        return ResponseEntity.ok(resultStatus);
    }


    @PatchMapping("/finish/{lessonId}")
    public ResponseEntity<?> finish (@PathVariable String lessonId){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [learning record finish]", member.getName(), member.getId());

        ResultStatus resultStatus = new ResultStatus();

        int count = learningRecordService.finish(lessonId);

        if (count > 0) {
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
        } else {
            resultStatus.setCode("C999");
            resultStatus.setMessage("儲存失敗");
        }
        return ResponseEntity.ok(resultStatus);
    }
}
