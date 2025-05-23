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
    String nativeTranslation(String text);

    String translateSentence(String sentencesJson) throws JsonProcessingException;

    LLMChatResponseDTO genWelcomeMessage(LLMChatRequestDTO llmChatRequestDTO) throws JsonProcessingException;

    GrammarResponseDTO grammarCheck(String currentMsgContent, String previewMsgContent) throws JsonProcessingException;

    List<AdvanceSentencesResponseDTO> advanceSentences(String content) throws JsonProcessingException;

    LLMChatResponseDTO replyMsg(LLMChatRequestDTO llmChatRequestDTO) throws JsonProcessingException;

    Feedback feedback(String dialogueText) throws JsonProcessingException;

    LLMChatResponseDTO genGuidingSentence(LLMChatRequestDTO llmChatRequestDTO) throws JsonProcessingException;

    String genChatroomTitle(String dialogueText) throws JsonProcessingException;
}
