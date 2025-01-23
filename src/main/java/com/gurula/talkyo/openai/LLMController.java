package com.gurula.talkyo.openai;

import com.gurula.talkyo.exception.ResultStatus;
import com.gurula.talkyo.openai.dto.TranslateRequestDTO;
import com.gurula.talkyo.member.Member;
import com.gurula.talkyo.member.MemberContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/openai")
public class LLMController {
    Logger logger = LoggerFactory.getLogger(LLMController.class);
    private final LLMService llmService;

    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }

    /**
     * 用母語寫出想法，翻譯成英文
     * @param dto
     * @return
     */
    @PostMapping("/translate")
    public ResponseEntity<?> translate (@RequestBody TranslateRequestDTO dto){
        final Member member = MemberContext.getMember();
        if (member != null) {
            logger.info("[{} | {}] 使用 [translate]", member.getName(), member.getId());
        }

        String translation = llmService.translate(dto.getText());

        ResultStatus<String> resultStatus = new ResultStatus<>();
        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(translation);
        return ResponseEntity.ok(resultStatus);
    }
}
