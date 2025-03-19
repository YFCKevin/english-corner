package com.gurula.talkyo.snapshot;

import com.gurula.talkyo.chatroom.ChatroomController;
import com.gurula.talkyo.chatroom.Message;
import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberContext;
import com.gurula.talkyo.snapshot.dto.SnapshotDTO;
import com.gurula.talkyo.snapshot.dto.SnapshotResponseDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/snapshot")
public class SnapshotController {

    private final Logger logger = LoggerFactory.getLogger(SnapshotController.class);
    private final SnapshotService snapshotService;

    public SnapshotController(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createLink(@RequestBody SnapshotDTO snapshotDTO) {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [create link]", member.getName(), member.getId());

        SnapshotForm snapshotForm = snapshotService.createLink(snapshotDTO, member);

        ResultStatus<SnapshotForm> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(snapshotForm);
        return ResponseEntity.ok(resultStatus);
    }



    @PatchMapping("/update")
    public ResponseEntity<?> update(@RequestBody SnapshotDTO snapshotDTO) {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [update link]", member.getName(), member.getId());

        String link = snapshotService.updateLink(snapshotDTO);
        ResultStatus<String> resultStatus = new ResultStatus<>();

        if (StringUtils.isNotBlank(link)) {
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
            resultStatus.setData(link);
        } else {
            resultStatus.setCode("C999");
            resultStatus.setMessage("系統錯誤");
        }

        return ResponseEntity.ok(resultStatus);
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id){
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [delete link]", member.getName(), member.getId());

        int count = snapshotService.deleteLink(id);
        ResultStatus<Void> resultStatus = new ResultStatus<>();

        if (count > 0) {
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
        } else {
            resultStatus.setCode("C999");
            resultStatus.setMessage("系統錯誤");
        }

        return ResponseEntity.ok(resultStatus);
    }



    @GetMapping("/all")
    public ResponseEntity<?> all() {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [all link]", member.getName(), member.getId());

        List<SnapshotForm> snapshotForms = snapshotService.getAllLinks(member.getId());
        List<SnapshotResponseDTO> snapshotResponseDTOList = new ArrayList<>();
        snapshotResponseDTOList = snapshotForms.stream()
                .map(snapshotForm -> new SnapshotResponseDTO(
                        snapshotForm.getId(),
                        snapshotForm.getTitle(),
                        snapshotForm.getCreationDateTime() != null ? snapshotForm.getCreationDateTime() : snapshotForm.getModificationDateTime(),
                        snapshotForm.getSnapshotType().getLabel(),
                        snapshotForm.getLink()
                )).toList();

        ResultStatus<List<SnapshotResponseDTO>> resultStatus = new ResultStatus<>();

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(snapshotResponseDTOList);
        return ResponseEntity.ok(resultStatus);
    }



    @GetMapping("/info/{chatroomId}")
    public ResponseEntity<?> info(@PathVariable String chatroomId) {
        final Member member = MemberContext.getMember();
        logger.info("[{} {}] [link info]", member.getName(), member.getId());

        SnapshotForm snapshotForm = snapshotService.info(chatroomId, member.getId());

        ResultStatus<SnapshotForm> resultStatus = new ResultStatus<>();

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(snapshotForm);
        return ResponseEntity.ok(resultStatus);
    }



    @GetMapping("/link/{link}")
    public ResponseEntity<?> link(@PathVariable String link) {

        ResultStatus<List<Map<Integer, SnapshotDetail>>> resultStatus = new ResultStatus<>();

        SnapshotForm snapshotForm = snapshotService.getInfoByLink(link);
        if (StringUtils.isNotBlank(snapshotForm.getId())) {
            List<Map<Integer, SnapshotDetail>> messages = snapshotForm.getSnapshotDetails().stream()
                    .map(detail -> Collections.singletonMap(1, detail))
                    .toList();
            resultStatus.setCode("C000");
            resultStatus.setMessage("成功");
            resultStatus.setData(messages);
        } else {    // 公開連結已被刪除
            resultStatus.setCode("C004");
            resultStatus.setMessage("公開連結已被刪除");
        }
        return ResponseEntity.ok(resultStatus);
    }
}
