package com.music.project.service;

import com.music.project.config.GeminiConfig;
import com.music.project.constant.AMConst;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final GeminiConfig geminiConfig;

    public GeminiService(WebClient.Builder webClientBuilder, GeminiConfig geminiConfig) {
        this.webClient = webClientBuilder.baseUrl(geminiConfig.getBaseurl()).build();
        this.geminiConfig = geminiConfig;
    }

    public Mono<String> callGeminiPrompt(String lyrics) {
        lyrics = lyrics.replaceAll("\"", "'");
        String prompt = AMConst.GEMINI_TEXT_PROMPT + lyrics;
        String url = ":generateContent?key=" + geminiConfig.getKey();
        String requestBody = """
            {
              "contents": [{
                "parts": [{"text": "%s"}]
              }]
            }
            """.formatted(prompt);
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Errore chiamata API Gemini", e)));
    }

}
