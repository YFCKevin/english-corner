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
    public String nativeTranslation(String text) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = nativeTranslatePayload(text);

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

    @Override
    public String genChatroomTitle(String dialogueText) throws JsonProcessingException {

        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(configProperties.getOpenaiApiKey());

        String payload = chatroomTitlePayload(dialogueText);

        System.out.println("payload = " + payload);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<ChatCompletionResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatCompletionResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("OpenAI回傳的status code: {}", response);
            ChatCompletionResponse responseBody = response.getBody();
            final String title = extractJsonContent(responseBody);
            System.out.println("title = " + title);
            return title;
        }

        return null;
    }

    private String chatroomTitlePayload(String dialogueText) {
        String systemMessageContent = "你是一位專業的摘要撰寫助手，擅長從對話中提取關鍵資訊並撰寫簡潔且自然的繁體中文摘要。" +
                "請確保摘要內容**極度簡潔**，用最少的詞語表達對話的核心。" +
                "摘要必須限於**最長15字**，且**只能用一句話**，請勿拆分成多個句子。" +
                "請僅返回摘要，不要添加額外的格式、標點符號或解釋。";

        String userMessageContent = String.format(
                "請根據以下對話，撰寫**最長15字**的繁體中文摘要：\n\n%s\n\n" +
                        "摘要必須**極度簡潔**，但仍清楚傳達對話核心內容。" +
                        "請勿添加引號、額外符號或解釋，只需返回摘要內容。",
                dialogueText
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

    private String guidingSentencePayload(String partnerAskMsg, Scenario scenario) {
        String systemMessageContent = String.format(
                "You're an AI assistant designed to simulate natural, real-life conversations, helping users practice everyday dialogues in different scenarios.\n\n" +
                        "### Role Setup\n" +
                        "- Your role: \"%s\"\n" +
                        "- User's role: \"%s\"\n" +
                        "- Scenario: \"%s\"\n\n" +
                        "### How to Respond\n" +
                        "1. Stay fully in character and respond naturally, just like a real person would.\n" +
                        "2. Keep the conversation smooth and engaging—don't sound robotic or scripted.\n" +
                        "3. Let things flow naturally, and if the conversation feels like it should wrap up, end it in a natural way.\n\n" +
                        "### Response Format\n" +
                        "Reply in the following JSON format:\n" +
                        "{\n" +
                        "    \"content\": \"[Your reply in casual, native-like spoken English]\",\n" +
                        "    \"translation\": \"[Your reply translated into Traditional Chinese]\"\n" +
                        "}",
                scenario.getHumanRole(),
                scenario.getPartnerRole(),
                scenario.getSubject()
        );

        String userMessageContent = String.format(
                "### Conversation Context\n" +
                        "- Scenario: \"%s\"\n" +
                        "- Your Role: \"%s\"\n" +
                        "- User's Role: \"%s\"\n\n" +
                        "### Last User Message:\n" +
                        "\"%s\"\n\n" +
                        "### Your Task\n" +
                        "Reply in a natural, conversational way that fits the flow and tone of the chat. Keep it engaging and realistic.\n" +
                        "Keep your response **brief (under 20 words), casual, and native-like.**",
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
                "Translate *only* the feedback into Traditional Chinese. Do *not* translate the user's original sentences—this is strictly prohibited. " +
                "Do *not* evaluate or reference the partner's sentences. Your response must adhere to the following strict rules:\n\n" +
                "1. **Do *not* translate or modify the user's original sentences under any circumstances.**\n" +
                "2. **Do *not* include the user's original sentences in the response.**\n" +
                "3. **Only provide feedback on the user's language use. Do not paraphrase or rewrite their sentences.**\n\n" +
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
                        "Evaluate *only* the user's sentences, providing detailed feedback as specified above. " +
                        "**Do not translate the user's sentences. Only provide feedback on their correctness.**",
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
        String systemMessageContent = "You are a helpful assistant that rewrites sentences in both informal and formal spoken styles, ensuring that each version sounds natural to a native speaker in conversation.";
        String userMessageContent = String.format(
                "Please rewrite the following sentence into two new sentences: one in an informal spoken style and one in a formal spoken style. Ensure that both sentences sound natural to native speakers in conversational contexts. Provide an explanation for why each rewritten sentence better suits its respective style compared to the original sentence. Return the result as an array of JSON objects in the following format: \n" +
                        "[\n" +
                        "  {\n" +
                        "    \"formal\": false,\n" +
                        "    \"sentence\": \"<informal spoken sentence>\",\n" +
                        "    \"explanation\": \"<reason why the informal sentence is more natural and conversational compared to the original sentence>\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"formal\": true,\n" +
                        "    \"sentence\": \"<formal spoken sentence>\",\n" +
                        "    \"explanation\": \"<reason why the formal sentence is more polished and professional while still sounding natural in spoken language>\"\n" +
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
                "Please check the grammatical correctness of the following sentence, considering the context provided by the previous sentence. " +
                        "If there is any mistake, even a minor one, correct it while preserving the original meaning. Then, translate the corrected sentence into Traditional Chinese " +
                        "and explain the grammatical mistake in Traditional Chinese. Ensure the explanation is precise and specifies the incorrect part and the reason. " +
                        "Also, check if the sentence is natural and fluent, and improve it if necessary. " +
                        "Return the result strictly in the following JSON format:\n\n" +
                        "{\n" +
                        "  \"correctSentence\": \"<corrected sentence>\",\n" +
                        "  \"translation\": \"<corrected sentence in Traditional Chinese>\",\n" +
                        "  \"errorReason\": \"<explanation of the error in Traditional Chinese>\"\n" +
                        "}\n\n" +
                        "If the input sentence is already grammatically correct, do not change it. However, only consider a sentence correct if it is completely free of grammar, punctuation, or phrasing issues. " +
                        "If there is even a slight mistake, correct it. Return the following JSON format:\n\n" +
                        "{\n" +
                        "  \"correctSentence\": \"<original sentence>\",\n" +
                        "  \"translation\": \"<original sentence in Traditional Chinese>\",\n" +
                        "  \"errorReason\": \"\"\n" +
                        "}\n\n" +
                        "Highlight the incorrect parts in the explanation, and ensure the reasoning is clear and specific. " +
                        "Return only a valid JSON object with no additional text.\n\n" +
                        "Previous sentence: %s\n" +
                        "Original sentence: %s",
                previewMsgContent, currentMsgContent
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

        String systemMessageContent = "You are a fluent English speaker. Your task is to translate the given text into natural, conversational English that sounds like something a native speaker would say. Keep it smooth, natural, and contextually appropriate. Only return the translated text—no extra formatting, quotation marks, or explanations.";

        String userMessageContent = String.format(
                "Translate the following text into natural, spoken English:\n\n%s\n\nMake sure it sounds like something a native speaker would actually say. No quotation marks, no extra symbols—just the translation.", text
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

    private String nativeTranslatePayload(String text) {
        String systemMessageContent = "You are a professional translator. Your task is to translate the given English text into natural and grammatically correct Traditional Chinese. Ensure the translation sounds fluent and contextually appropriate. Only return the translated text without any additional formatting, quotation marks, or explanations.";

        String userMessageContent = String.format("Translate the following English text into proper Traditional Chinese:\n\n%s\n\nDo not add quotation marks or extra symbols. Only return the translation.", text);

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
