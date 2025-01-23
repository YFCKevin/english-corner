package com.gurula.talkyo.openai;

import com.gurula.talkyo.openai.dto.LLMChatRequestDTO;
import com.gurula.talkyo.openai.dto.LLMChatResponseDTO;

public interface LLMService {
    String translate(String text);

    String translateSentence(String sentencesJson);

    LLMChatResponseDTO genWelcomeMessage(LLMChatRequestDTO llmChatRequestDTO);
}
