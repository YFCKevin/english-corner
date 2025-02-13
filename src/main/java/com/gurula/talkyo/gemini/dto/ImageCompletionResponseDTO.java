package com.gurula.talkyo.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageCompletionResponseDTO {

    @JsonProperty("candidates")
    private List<Candidate> candidates;

    @JsonProperty("usageMetadata")
    private UsageMetadata usageMetadata;

    @JsonProperty("modelVersion")
    private String modelVersion;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public UsageMetadata getUsageMetadata() {
        return usageMetadata;
    }

    public void setUsageMetadata(UsageMetadata usageMetadata) {
        this.usageMetadata = usageMetadata;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        @JsonProperty("content")
        private Content content;

        @JsonProperty("finishReason")
        private String finishReason;

        @JsonProperty("avgLogprobs")
        private Double avgLogprobs;

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }

        public String getFinishReason() {
            return finishReason;
        }

        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }

        public Double getAvgLogprobs() {
            return avgLogprobs;
        }

        public void setAvgLogprobs(Double avgLogprobs) {
            this.avgLogprobs = avgLogprobs;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;

        @JsonProperty("role")
        private String role;

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        @JsonProperty("text")
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageMetadata {
        @JsonProperty("promptTokenCount")
        private Integer promptTokenCount;

        @JsonProperty("candidatesTokenCount")
        private Integer candidatesTokenCount;

        @JsonProperty("totalTokenCount")
        private Integer totalTokenCount;

        @JsonProperty("promptTokensDetails")
        private List<TokenDetails> promptTokensDetails;

        @JsonProperty("candidatesTokensDetails")
        private List<TokenDetails> candidatesTokensDetails;

        public Integer getPromptTokenCount() {
            return promptTokenCount;
        }

        public void setPromptTokenCount(Integer promptTokenCount) {
            this.promptTokenCount = promptTokenCount;
        }

        public Integer getCandidatesTokenCount() {
            return candidatesTokenCount;
        }

        public void setCandidatesTokenCount(Integer candidatesTokenCount) {
            this.candidatesTokenCount = candidatesTokenCount;
        }

        public Integer getTotalTokenCount() {
            return totalTokenCount;
        }

        public void setTotalTokenCount(Integer totalTokenCount) {
            this.totalTokenCount = totalTokenCount;
        }

        public List<TokenDetails> getPromptTokensDetails() {
            return promptTokensDetails;
        }

        public void setPromptTokensDetails(List<TokenDetails> promptTokensDetails) {
            this.promptTokensDetails = promptTokensDetails;
        }

        public List<TokenDetails> getCandidatesTokensDetails() {
            return candidatesTokensDetails;
        }

        public void setCandidatesTokensDetails(List<TokenDetails> candidatesTokensDetails) {
            this.candidatesTokensDetails = candidatesTokensDetails;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenDetails {
        @JsonProperty("modality")
        private String modality;

        @JsonProperty("tokenCount")
        private Integer tokenCount;

        public String getModality() {
            return modality;
        }

        public void setModality(String modality) {
            this.modality = modality;
        }

        public Integer getTokenCount() {
            return tokenCount;
        }

        public void setTokenCount(Integer tokenCount) {
            this.tokenCount = tokenCount;
        }
    }
}
