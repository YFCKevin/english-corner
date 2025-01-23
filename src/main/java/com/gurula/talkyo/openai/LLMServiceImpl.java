package com.gurula.talkyo.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gurula.talkyo.chatroom.Scenario;
import com.gurula.talkyo.openai.dto.*;
import com.gurula.talkyo.openai.enums.Role;
import com.gurula.talkyo.properties.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class LLMServiceImpl implements LLMService{
    private final Logger logger = LoggerFactory.getLogger(LLMServiceImpl.class);
    private final ConfigProperties configProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public LLMServiceImpl(ConfigProperties configProperties, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.configProperties = configProperties;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public String translate(String text) {
        return null;
    }

    @Override
    public String translateSentence(String sentence) {
        return null;
    }

    @Override
    public LLMChatResponseDTO genWelcomeMessage(LLMChatRequestDTO llmChatRequestDTO) {

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = createPayload(llmChatRequestDTO);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final LLMChatResponseDTO responseDTO = extractJsonContent(responseBody);
            System.out.println("GPT回傳資料 ======> " + responseDTO.getContent() + "\n" + responseDTO.getTranslation());
            return responseDTO;
        }

        return null;
    }


    private String createPayload(LLMChatRequestDTO llmChatRequestDTO) {
        final String sentences = llmChatRequestDTO.getSentences();
        final Scenario scenario = llmChatRequestDTO.getScenario();

        String systemMessageContent = String.format(
                "You are a %s assisting a %s who is %s. " +
                        "You will help the %s learn English phrases related to %s by providing both the sentence in English and its Traditional Chinese translation. " +
                        "For each sentence, generate a question related to the sentence and ask the user to respond in English. " +
                        "After each question, provide the sentence in English and its Traditional Chinese translation. " +
                        "Your task is to guide the %s through typical scenarios, using sentences from the following list. " +
                        "Please respond in the following format:\n" +
                        "{\n" +
                        "    \"content\": \"[Question in English]\",\n" +
                        "    \"translation\": \"[Traditional Chinese translation of the question]\"\n" +
                        "}",
                scenario.getPartnerRole(),
                scenario.getHumanRole(),
                scenario.getSubject(),
                scenario.getHumanRole(),
                scenario.getSubject(),
                scenario.getHumanRole()
        );


        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, sentences);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }



    private LLMChatResponseDTO extractJsonContent(ChatCompletionResponse responseBody) {
        if (responseBody != null && !responseBody.getChoices().isEmpty()) {
            ChatCompletionResponse.Choice choice = responseBody.getChoices().get(0);
            if (choice != null && choice.getMessage() != null) {
                String content = choice.getMessage().getContent().trim();

                // 去掉反引號
                if (content != null) {
                    content = content.replace("```json", "").replace("```", "").trim();
                }

                try {
                    return objectMapper.readValue(content, LLMChatResponseDTO.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
