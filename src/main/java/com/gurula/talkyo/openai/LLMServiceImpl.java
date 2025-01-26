package com.gurula.talkyo.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gurula.talkyo.chatroom.Feedback;
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
    public LLMChatResponseDTO genWelcomeMessage(LLMChatRequestDTO llmChatRequestDTO) throws JsonProcessingException {

        final Scenario scenario = llmChatRequestDTO.getScenario();
        final String sentences = llmChatRequestDTO.getSentences();

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = firstMsgPayload(scenario, sentences);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final String jsonContent = extractJsonContent(responseBody);
            final LLMChatResponseDTO responseDTO = objectMapper.readValue(jsonContent, LLMChatResponseDTO.class);
            System.out.println("GPT回傳資料 ======> " + responseDTO.getContent() + "\n" + responseDTO.getTranslation());
            return responseDTO;
        }

        return null;
    }

    @Override
    public GrammarResponseDTO grammarCheck(String content) throws JsonProcessingException {

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = grammarCheckPayload(content);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final String jsonContent = extractJsonContent(responseBody);
            final GrammarResponseDTO responseDTO = objectMapper.readValue(jsonContent, GrammarResponseDTO.class);
            System.out.println("GPT回傳資料 ======> " + responseDTO.getCorrectSentence() + "\n" + responseDTO.getTranslation() + "\n" + responseDTO.getErrorReason());
            return responseDTO;
        }

        return null;
    }

    @Override
    public List<AdvanceSentencesResponseDTO> advanceSentences(String correctSentence) throws JsonProcessingException {

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = advancedPayload(correctSentence);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final String jsonContent = extractJsonContent(responseBody);
            final List<AdvanceSentencesResponseDTO> responseDTOS = objectMapper.readValue(jsonContent, new TypeReference<>() {
            });
            System.out.println("GPT回傳資料 ======> " + responseDTOS);
            return responseDTOS;
        }

        return null;
    }

    @Override
    public LLMChatResponseDTO replyMsg(LLMChatRequestDTO llmChatRequestDTO) throws JsonProcessingException {

        final String historyMsgs = llmChatRequestDTO.getHistoryMsgs();
        final Scenario scenario = llmChatRequestDTO.getScenario();

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = replyPayload(historyMsgs, scenario);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final String jsonContent = extractJsonContent(responseBody);
            final LLMChatResponseDTO responseDTO = objectMapper.readValue(jsonContent, LLMChatResponseDTO.class);
            System.out.println("GPT回傳資料 ======> " + responseDTO.getContent() + "\n" + responseDTO.getTranslation());
            return responseDTO;
        }

        return null;
    }

    @Override
    public Feedback feedback(String dialogueText) throws JsonProcessingException {

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = feedbackPayload(dialogueText);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final String jsonContent = extractJsonContent(responseBody);
            final Feedback responseDTO = objectMapper.readValue(jsonContent, Feedback.class);
            System.out.println("GPT回傳資料 ======> " + responseDTO.getComment() + "\n" + responseDTO.getTranslation());
            return responseDTO;
        }

        return null;
    }

    private String feedbackPayload(String dialogueText) {
        // System message content
        String systemMessageContent = "You are a language learning assistant. Your task is to evaluate only the user's sentences from the provided dialogue. " +
                "Focus on identifying both errors and correct usage in the user's sentences. Provide feedback in a concise, bullet-point format with approximately 300 words. " +
                "Translate the feedback into Traditional Chinese. Do not evaluate or reference the partner's sentences. " +
                "Return the feedback in the following JSON format:\n" +
                "{\n" +
                "  \"comment\": \"[300-word English feedback in bullet-point format]\",\n" +
                "  \"translation\": \"[Traditional Chinese translation of the feedback]\"\n" +
                "}";

        String userMessageContent = String.format(
                "Here is the dialogue:\n%s\n" +
                        "Evaluate only the user's sentences, providing detailed feedback as specified above.",
                dialogueText
        );

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 1);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String replyPayload(String historyMsgs, Scenario scenario) {

        String systemMessageContent = String.format(
                "You are a %s assisting a %s who is %s. " +
                        "You will help the %s learn English phrases related to %s by providing both the sentence in English and its Traditional Chinese translation. " +
                        "For each sentence, generate a question related to the sentence and ask the user to respond in English. " +
                        "After each question, provide the sentence in English and its Traditional Chinese translation. " +
                        "Your task is to guide the %s through typical scenarios, using sentences from the following list. " +
                        "Make sure to refer to the previous conversation history provided below in order to generate a relevant response, " +
                        "and adjust your responses based on that context. " +
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

        // 將歷史訊息納入，用於 AI 的參考
        String userMessageContent = String.format(
                "Here is the history of our conversation:\n%s",
                historyMsgs
        );

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 1);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String advancedPayload(String correctSentence) {
        String systemMessageContent = "You are a helpful assistant that rewrites sentences in both informal and formal styles while explaining why the rewritten sentences better suit their respective styles.";
        String userMessageContent = String.format(
                "Please rewrite the following sentence into two new sentences: one in an informal tone and one in a formal tone. Provide an explanation for why each rewritten sentence better suits its respective tone compared to the original sentence. Return the result as an array of JSON objects in the following format: \n" +
                        "[\n" +
                        "  {\n" +
                        "    \"formal\": false,\n" +
                        "    \"sentence\": \"<informal sentence>\",\n" +
                        "    \"explanation\": \"<reason why the informal sentence is more informal compared to the original sentence>\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"formal\": true,\n" +
                        "    \"sentence\": \"<formal sentence>\",\n" +
                        "    \"explanation\": \"<reason why the formal sentence is more formal compared to the original sentence>\"\n" +
                        "  }\n" +
                        "]\n" +
                        "Original sentence: %s", correctSentence
        );

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 0.7F, 500);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String grammarCheckPayload(String content) {
        String systemMessageContent = "You are a helpful assistant that helps correct grammar and provides reasons for the errors.";
        String userMessageContent = String.format(
                "Please correct the grammar of the following sentence, translate the corrected sentence into Traditional Chinese, and explain the reason for the mistake in Traditional Chinese. Return the result in the following format: \n" +
                        "{\"correctSentence\": \"<corrected sentence>\", \"translation\": \"<corrected sentence in Traditional Chinese>\", \"errorReason\": \"<explanation of the error in Traditional Chinese>\"}\n" +
                        "If the input sentence is already grammatically correct, return the result in the following format: \n" +
                        "{\"correctSentence\": \"<original sentence>\", \"translation\": \"<original sentence in Traditional Chinese>\", \"errorReason\": \"\"}\n" +
                        "Do not modify the original sentence if it is correct. Always ensure that the errorReason is an empty string if there is no error.\n\n" +
                        "Original sentence: %s", content
        );

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 0.3F, 500);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String firstMsgPayload(Scenario scenario, String sentences) {

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

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 1);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }



    private String extractJsonContent(ChatCompletionResponse responseBody) {
        if (responseBody != null && !responseBody.getChoices().isEmpty()) {
            ChatCompletionResponse.Choice choice = responseBody.getChoices().get(0);
            if (choice != null && choice.getMessage() != null) {
                String content = choice.getMessage().getContent().trim();

                // 去掉反引號
                if (content != null) {
                    content = content.replace("```json", "").replace("```", "").trim();
                }

                return content;
            }
        }
        return null;
    }


}
