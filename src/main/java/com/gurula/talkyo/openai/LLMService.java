package com.gurula.talkyo.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gurula.talkyo.chatroom.Feedback;
import com.gurula.talkyo.openai.dto.AdvanceSentencesResponseDTO;
import com.gurula.talkyo.openai.dto.GrammarResponseDTO;
import com.gurula.talkyo.openai.dto.LLMChatRequestDTO;
import com.gurula.talkyo.openai.dto.LLMChatResponseDTO;

import java.util.List;

public interface LLMService {
    String translate(String text);

    String translateSentence(String sentencesJson);

    LLMChatResponseDTO genWelcomeMessage(LLMChatRequestDTO llmChatRequestDTO) throws JsonProcessingException;

    GrammarResponseDTO grammarCheck(String content) throws JsonProcessingException;

    List<AdvanceSentencesResponseDTO> advanceSentences(String correctSentence) throws JsonProcessingException;

    LLMChatResponseDTO replyMsg(LLMChatRequestDTO llmChatRequestDTO) throws JsonProcessingException;

    Feedback feedback(String dialogueText) throws JsonProcessingException;
}
