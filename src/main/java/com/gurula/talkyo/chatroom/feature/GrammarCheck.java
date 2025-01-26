package com.gurula.talkyo.chatroom.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.chatroom.*;
import com.gurula.talkyo.chatroom.dto.ChatRequestDTO;
import com.gurula.talkyo.chatroom.dto.ConversationChainDTO;
import com.gurula.talkyo.chatroom.factory.AbstractConversation;
import com.gurula.talkyo.openai.LLMService;
import com.gurula.talkyo.openai.dto.GrammarResponseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Component
public class GrammarCheck implements ConversationFeature{
    private final LLMService llmService;
    private final SimpleDateFormat sdf;
    private final MessageRepository messageRepository;

    public GrammarCheck(LLMService llmService, @Qualifier("sdf") SimpleDateFormat sdf,
                        MessageRepository messageRepository) {
        this.llmService = llmService;
        this.sdf = sdf;
        this.messageRepository = messageRepository;
    }

    @Override
    public <T extends AbstractConversation> ConversationChainDTO execute(T conversation, ChatRequestDTO chatRequestDTO) throws JsonProcessingException {
        System.out.println("GrammarCheck");

        final String messageId = chatRequestDTO.getMessageId();
        final Optional<Message> opt = messageRepository.findById(messageId);
        if (opt.isPresent()) {
            final Message message = opt.get();
            String grammarInputText;
            if (message.getMessageContents().stream().anyMatch(content -> content instanceof AudioContent)) {
                // 如果存在 AudioContent，取 parsedText
                grammarInputText = message.getMessageContents().stream()
                        .filter(content -> content instanceof AudioContent)
                        .map(content -> ((AudioContent) content).getParsedText())
                        .findFirst().orElse(null);
            } else {
                // 否則取 TextContent 的 text
                grammarInputText = message.getMessageContents().stream()
                        .filter(content -> content instanceof TextContent)
                        .map(content -> ((TextContent) content).getText())
                        .findFirst().orElse(null);
            }

            GrammarResponseDTO grammarResponseDTO = llmService.grammarCheck(grammarInputText);

            if (StringUtils.isNotBlank(grammarResponseDTO.getErrorReason())) {  // 代表文法有錯誤
                GrammarResult grammarResult = new GrammarResult();
                grammarResult.setCorrectSentence(grammarResponseDTO.getCorrectSentence());
                grammarResult.setErrorReason(grammarResponseDTO.getErrorReason());
                grammarResult.setTranslation(grammarResponseDTO.getTranslation());
                grammarResult.setCreationDate(sdf.format(new Date()));
                grammarResult.setErrorSentence(grammarInputText);
                message.setSenderContent(new HumanSender(grammarResult));
            } else {
                message.setAccuracy(true);
            }

            messageRepository.save(message);
        }

        return null;
    }
}
