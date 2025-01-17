package com.gurula.talkyo.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigProperties {
    @Value("${config.openaiApiKey}")
    private String openaiApiKey;
    @Value("${config.jsonPath}")
    private String jsonPath;
    @Value("${config.picSavePath}")
    private String picSavePath;
    @Value("${config.picShowPath}")
    private String picShowPath;
    @Value("${config.audioSavePath}")
    private String audioSavePath;
    @Value("${config.audioShowPath}")
    private String audioShowPath;
    @Value("${config.globalDomain}")
    private String globalDomain;
    @Value("${spring.data.mongodb.uri}")
    private String mongodbUri;
    @Value("${spring.security.oauth2.client.registration.line.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.line.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.provider.line.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.provider.line.user-info-uri}")
    private String userInfoUri;
    @Value("${config.geminiApiKey}")
    private String geminiApiKey;
    @Value("${config.cloudTranslateApiKey}")
    private String cloudTranslateApiKey;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getOpenaiApiKey() {
        return openaiApiKey;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public String getPicSavePath() {
        return picSavePath;
    }

    public String getPicShowPath() {
        return picShowPath;
    }

    public String getGlobalDomain() {
        return globalDomain;
    }

    public String getMongodbUri() {
        return mongodbUri;
    }

    public String getAudioSavePath() {
        return audioSavePath;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public String getAudioShowPath() {
        return audioShowPath;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public String getCloudTranslateApiKey() {
        return cloudTranslateApiKey;
    }
}
