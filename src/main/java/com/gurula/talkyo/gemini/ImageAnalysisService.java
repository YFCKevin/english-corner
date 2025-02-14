package com.gurula.talkyo.gemini;

import com.gurula.talkyo.gemini.dto.ImageCompletionResponseDTO;
import com.gurula.talkyo.gemini.utils.GeminiUtil;
import com.gurula.talkyo.properties.ConfigProperties;
import com.gurula.talkyo.properties.GeminiProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageAnalysisService {
    private final GeminiProperties geminiProperties;
    private final RestTemplate restTemplate;
    private final ConfigProperties configProperties;

    public ImageAnalysisService(GeminiProperties geminiProperties, RestTemplate restTemplate, ConfigProperties configProperties) {
        this.geminiProperties = geminiProperties;
        this.restTemplate = restTemplate;
        this.configProperties = configProperties;
    }

    public String imageAnalysis (String imageName, String text){

        String base64Image = GeminiUtil.encodeImageToBase64(configProperties.getPicSavePath() + imageName);
        if (base64Image == null) {
            System.out.println("無法讀取圖片");
            return "解析失敗";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", (text == null || text.isEmpty()) ? "Analyze this image" : text);

        Map<String, Object> imagePart = new HashMap<>();
        imagePart.put("inline_data", new HashMap<String, Object>() {{
            put("mime_type", "image/jpeg");
            put("data", base64Image);
        }});

        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(textPart);
        parts.add(imagePart);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);

        contents.add(content);
        requestBody.put("contents", contents);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ImageCompletionResponseDTO> response = restTemplate.exchange(
                geminiProperties.getImage().getUrl() + geminiProperties.getImage().getAnalytics().getKey(),
                HttpMethod.POST,
                entity,
                ImageCompletionResponseDTO.class
        );

        final String responseText = response.getBody().getCandidates().get(0).getContent().getParts().get(0).getText();
        System.out.println(responseText);

        return responseText;
    }
}
