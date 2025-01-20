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

    public void setOpenaiApiKey(String openaiApiKey) {
        this.openaiApiKey = openaiApiKey;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public void setPicSavePath(String picSavePath) {
        this.picSavePath = picSavePath;
    }

    public void setPicShowPath(String picShowPath) {
        this.picShowPath = picShowPath;
    }

    public void setAudioSavePath(String audioSavePath) {
        this.audioSavePath = audioSavePath;
    }

    public void setAudioShowPath(String audioShowPath) {
        this.audioShowPath = audioShowPath;
    }

    public void setGlobalDomain(String globalDomain) {
        this.globalDomain = globalDomain;
    }

    public void setMongodbUri(String mongodbUri) {
        this.mongodbUri = mongodbUri;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    public void setGeminiApiKey(String geminiApiKey) {
        this.geminiApiKey = geminiApiKey;
    }

    public void setCloudTranslateApiKey(String cloudTranslateApiKey) {
        this.cloudTranslateApiKey = cloudTranslateApiKey;
    }
}
