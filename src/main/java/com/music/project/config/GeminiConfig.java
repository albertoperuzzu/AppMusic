package com.music.project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gemini")
public class GeminiConfig {

    private String key;
    private String baseurl;

    public String getKey() {
        return key;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

}
