package com.gurula.talkyo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "azure.ai")
public class AzureProperties {

    private final AudioProperties audio = new AudioProperties();
    private final TextProperties text = new TextProperties();

    public AudioProperties getAudio() {
        return audio;
    }

    public TextProperties getText() {
        return text;
    }

    public static class AudioProperties {
        private String key;
        private String region;

        public String getKey() {
            return key;
        }

        public String getRegion() {
            return region;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }

    public static class TextProperties {
        private AnalyticsProperties analytics = new AnalyticsProperties();
        private String endpoint;

        public AnalyticsProperties getAnalytics() {
            return analytics;
        }
        public String getEndpoint() {
            return endpoint;
        }

        public void setAnalytics(AnalyticsProperties analytics) {
            this.analytics = analytics;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public static class AnalyticsProperties {
            private String key;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }
    }
}
