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

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = translatePayload(text);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final String jsonContent = extractJsonContent(responseBody);
            System.out.println("GPT回傳資料 ======> " + jsonContent);
            return jsonContent;
        }

        return null;
    }

    @Override
    public String translateSentence(String sentence) {

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = translateSentencePayload(sentence);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final String jsonContent = extractJsonContent(responseBody);
            System.out.println("GPT回傳資料 ======> " + jsonContent);
            return jsonContent;
        }

        return null;
    }

    private String translateSentencePayload(String sentence) {

        String systemMessageContent = "You are a skilled assistant that translates sentences from English to Traditional Chinese. Please ensure that the translation is clear, accurate, and culturally appropriate for native speakers. Maintain the tone and context of the original sentence as much as possible.";

        String userMessageContent = String.format(
                "Please translate the following English sentences into Traditional Chinese. Return the result as a JSON array of objects, where each object contains the following properties:\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"unitNumber\": \"<unitNumber>\",\n" +
                        "    \"content\": \"<original sentence>\",\n" +
                        "    \"translation\": \"<translated sentence>\"\n" +
                        "  },\n" +
                        "]\n" +
                        "For each sentence, return the translation along with the original sentence.\n" +
                        "Here are the sentences to be translated:\n" +
                        "%s", sentence
        );

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 0.7F, 5000);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
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
    public GrammarResponseDTO grammarCheck(String currentMsgContent, String previewMsgContent) throws JsonProcessingException {

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = grammarCheckPayload(currentMsgContent, previewMsgContent);

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
            System.out.println("jsonContent = " + jsonContent);
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

    @Override
    public LLMChatResponseDTO genGuidingSentence(LLMChatRequestDTO llmChatRequestDTO) throws JsonProcessingException {
        final String partnerAskMsg = llmChatRequestDTO.getHistoryMsgs();
        final Scenario scenario = llmChatRequestDTO.getScenario();

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = guidingSentencePayload(partnerAskMsg, scenario);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final String jsonContent = extractJsonContent(responseBody);
            System.out.println("jsonContent = " + jsonContent);
            final LLMChatResponseDTO responseDTO = objectMapper.readValue(jsonContent, LLMChatResponseDTO.class);
            System.out.println("GPT回傳資料 ======> " + responseDTO.getContent() + "\n" + responseDTO.getTranslation());
            return responseDTO;
        }

        return null;
    }

    private String guidingSentencePayload(String partnerAskMsg, Scenario scenario) {
        String systemMessageContent = String.format(
                "You are an AI assistant, responsible for simulating role-playing conversations to help the user practice conversation skills in a specific scenario.\n\n" +
                        "### Role Setting\n" +
                        "- Your role: \"%s\"\n" +
                        "- User's role: \"%s\"\n" +
                        "- Scenario: \"%s\"\n\n" +
                        "### Conversation Guidelines\n" +
                        "1. Stay strictly in your assigned role.\n" +
                        "2. Always generate a natural and contextually relevant response.\n" +
                        "3. Conclude the conversation naturally at an appropriate point.\n" +
                        "### Response Format\n" +
                        "Your response must be in the following structured JSON format:\n" +
                        "{\n" +
                        "    \"content\": \"[Your response in English]\",\n" +
                        "    \"translation\": \"[Your response translated into Traditional Chinese]\"\n" +
                        "}",
                scenario.getHumanRole(),    // AI role
                scenario.getPartnerRole(),  // User role
                scenario.getSubject()       // Scenario subject
        );

        String userMessageContent = String.format(
                "### Current Conversation Context\n" +
                        "- Scenario: \"%s\"\n" +
                        "- Your Role: \"%s\"\n" +
                        "- User's Role: \"%s\"\n\n" +
                        "### Last User Message:\n" +
                        "\"%s\"\n\n" +
                        "### Your Task:\n" +
                        "Based on the previous message from the user, generate a natural and relevant response following the conversation flow and maintaining the scenario's context.\n" +
                        "**Your response must be concise and within 20 words.**",
                scenario.getSubject(),
                scenario.getHumanRole(),
                scenario.getPartnerRole(),
                partnerAskMsg
        );

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 1, 5000);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String feedbackPayload(String dialogueText) {

        String systemMessageContent = "You are a language learning assistant. Your task is to evaluate *only* the user's sentences from the provided dialogue. " +
                "Focus on identifying *both* errors and correct usage in the user's sentences. Provide feedback in a concise, bullet-point format (using '*' as the bullet point indicator) with approximately 300 words. " +
                "Translate *only* the feedback into Traditional Chinese. The translation should *also* be in a bullet-point format (using '*' as the bullet point indicator), with each bullet point being the translation of the corresponding English feedback point. Do *not* evaluate or reference the partner's sentences. Do *not* translate the user's original sentences. " +
                "Exclude *any* phrases of appreciation or gratitude, such as 'Thank you' or 'Great job'. " +
                "Ensure that both 'comment' and 'translation' are single strings, *not* arrays or lists. Each bullet point in *both* the English feedback and the Traditional Chinese translation should end with a `<br>` tag.  " +
                "*Specifically, each English feedback item should look like this: '* [Feedback point]<br>', and each Traditional Chinese translation item should look like this: '* [Translation point]<br>'.*\n" +
                "Return the feedback in the following *strict* JSON format:\n" +
                "{\n" +
                "  \"comment\": \"[300-word English feedback in bullet-point format *without* gratitude or appreciation statements, *each item ending with <br>*]\",\n" +
                "  \"translation\": \"[Traditional Chinese translation of the feedback *only*, do not translate the user's sentences, *in bullet-point format, each item ending with <br>*]\"\n" +
                "}";

        String userMessageContent = String.format(
                "Here is the dialogue:\n%s\n" +
                        "Evaluate *only* the user's sentences, providing detailed feedback as specified above.",
                dialogueText
        );

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 1, 5000);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String replyPayload(String historyMsgs, Scenario scenario) {

        String systemMessageContent = String.format(
                "You're an AI assistant designed to help the user practice conversation skills through role-playing.\n\n" +
                        "### Role Setup\n" +
                        "- Your role: \"%s\"\n" +
                        "- User's role: \"%s\"\n" +
                        "- Scenario: \"%s\"\n\n" +
                        "### Conversation Guidelines\n" +
                        "1. Stay in character and follow the scenario.\n" +
                        "2. Start with a natural opening line to kick off the conversation.\n" +
                        "3. Respond based on what the user says—don’t give sample answers too soon.\n" +
                        "4. If the user’s response is unclear or incomplete, ask for more details.\n" +
                        "5. If they’re stuck, give a partial example but leave room for them to complete it.\n" +
                        "6. If their answer is too simple, encourage them to elaborate or explore different angles.\n" +
                        "7. Wrap up the conversation naturally at the right time—don’t cut it off too early.\n" +
                        "8. Ask one question at a time and wait for their response before moving forward.\n" +
                        "9. Keep the flow natural—don’t overwhelm them with too many questions at once.\n\n" +
                        "### Response Format\n" +
                        "Reply in this format:\n" +
                        "{\n" +
                        "    \"content\": \"[Your response in English]\",\n" +
                        "    \"translation\": \"[Your response translated into Traditional Chinese]\"\n" +
                        "}",
                scenario.getPartnerRole(),  // AI role
                scenario.getHumanRole(),    // User role
                scenario.getSubject()       // Scenario subject
        );


        // 將歷史訊息納入，用於 AI 的參考
        String userMessageContent = String.format(
                "Conversation Scenario: \"%s\"\n" +
                        "Your Role: \"%s\"\n" +
                        "User's Role: \"%s\"\n\n" +
                        "History of the conversation:\n" +
                        "%s\n\n" +
                        "Please continue the conversation based on the previous messages.",
                scenario.getSubject(),
                scenario.getPartnerRole(),
                scenario.getHumanRole(),
                historyMsgs
        );

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 1, 5000);

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

    private String grammarCheckPayload(String currentMsgContent, String previewMsgContent) {
        String systemMessageContent = "You are a helpful assistant that helps correct grammar and provides reasons for the errors.";
        String userMessageContent = String.format(
                "Please correct the grammar of the following sentence, translate the corrected sentence into Traditional Chinese, and explain the reason for the mistake in Traditional Chinese. Consider the context provided by the previous sentence. Return the result in the following format: \n" +
                        "{\"correctSentence\": \"<corrected sentence>\", \"translation\": \"<corrected sentence in Traditional Chinese>\", \"errorReason\": \"<explanation of the error in Traditional Chinese>\"}\n" +
                        "If the input sentence is already grammatically correct, return the result in the following format: \n" +
                        "{\"correctSentence\": \"<original sentence>\", \"translation\": \"<original sentence in Traditional Chinese>\", \"errorReason\": \"\"}\n" +
                        "Do not modify the original sentence if it is correct. Always ensure that the errorReason is an empty string if there is no error.\n\n" +
                        "Previous sentence: %s\n" +
                        "Original sentence: %s", currentMsgContent, previewMsgContent
        );

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 0.3F, 5000);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String firstMsgPayload(Scenario scenario, String sentences) {

        String systemMessageContent = String.format(
                "You are an AI assistant designed to role-play conversations naturally and help the user practice real-life communication in a given scenario.\n\n" +
                        "**Your Role**\n" +
                        "- You are playing the role of: \"%s\" (e.g., a barista, hotel receptionist, interviewer).\n" +
                        "- The user is playing the role of: \"%s\" (e.g., a customer, traveler, job applicant).\n" +
                        "- The conversation scenario is: \"%s\" (e.g., ordering coffee, checking into a hotel, a job interview).\n\n" +
                        "**How to Respond**\n" +
                        "1. Stay in character and respond naturally, just like a native speaker would in a real conversation.\n" +
                        "2. **Start with a natural opening line that fits your role and the scenario.**\n" +
                        "3. Keep responses conversational—use contractions (e.g., \"I'm\" instead of \"I am\"), casual phrasing, and natural pauses.\n" +
                        "4. Wait for the user to reply before continuing. Don't provide complete sample answers upfront.\n" +
                        "5. If the user gives an incomplete response, gently prompt them to add more details.\n" +
                        "6. If the user seems stuck, offer a hint or a partial response to guide them, but let them complete the sentence.\n" +
                        "7. If the user's response is too simple, add a follow-up question or suggest an alternative to make the conversation more engaging.\n" +
                        "8. Wrap up the conversation naturally when appropriate, but don’t end it too soon.\n\n" +
                        "**Response Format**\n" +
                        "Reply in the following format:\n" +
                        "{\n" +
                        "    \"content\": \"[Your response in English, written in a natural and conversational way]\",\n" +
                        "    \"translation\": \"[Your response translated into Traditional Chinese]\"\n" +
                        "}",
                scenario.getPartnerRole(),
                scenario.getHumanRole(),
                scenario.getSubject()
        );

        String userMessageContent = String.format(
                "Conversation Scenario: \"%s\"\n" +
                        "Your Role: \"%s\"\n" +
                        "User's Role: \"%s\"\n\n" +
                        "Here are some example sentences for you to use as the \"%s\" role:\n" +
                        "%s\n\n" +
                        "Please start the conversation based on the scenario. Use one of the example sentences or create your own response to initiate the conversation.",
                scenario.getSubject(),
                scenario.getPartnerRole(),
                scenario.getHumanRole(),
                scenario.getHumanRole(),
                sentences
        );


        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 1, 2000);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String translatePayload(String text) {

        String systemMessageContent = "You are a professional translator. Your task is to translate the given text into natural and grammatically correct English. Ensure the translation sounds fluent and contextually appropriate. Only return the translated text without any additional formatting, quotation marks, or explanations.";

        String userMessageContent = String.format("Translate the following text into proper English:\n\n%s\n\nDo not add quotation marks or extra symbols. Only return the translation.", text);

        MsgDTO systemMessage = new MsgDTO(Role.system, systemMessageContent);
        MsgDTO userMessage = new MsgDTO(Role.user, userMessageContent);

        List<MsgDTO> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        PayloadDTO payload = new PayloadDTO("gpt-4o-mini", messages, 0.3F, 5000);

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
