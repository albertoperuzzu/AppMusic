package com.music.project.service;

import com.music.project.config.GeniusConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Map;
import java.util.List;
import com.music.project.util.GeniusScraper;

@Service
public class GeniusService {

    private final WebClient webClient;
    private final GeniusConfig geniusConfig;

    public GeniusService(WebClient.Builder webClientBuilder, GeniusConfig geniusConfig) {
        this.webClient = webClientBuilder.baseUrl("https://api.genius.com").build();
        this.geniusConfig = geniusConfig;
    }

    public String getLyricsLink(String query) {
        try {
            Map response = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search")
                            .queryParam("q", query)
                            .build())
                    .header("Authorization", "Bearer " + geniusConfig.getToken())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // Sincrono, otteniamo direttamente la risposta
            Map<String, Object> responseResponse = (Map<String, Object>) response.get("response");
            if (responseResponse != null && responseResponse.containsKey("hits")) {
                var hits = (List<Map<String, Object>>) responseResponse.get("hits");
                if (hits != null && !hits.isEmpty()) {
                    Map<String, Object> result = (Map<String, Object>) hits.get(0).get("result");
                    return (String) result.get("path");
                }
            }
            return null;
        } catch (WebClientResponseException e) {
            System.err.println("Errore nella risposta API: " + e.getMessage());
            return null;
        }
    }

    public String getLyrics(String geniusUrl) {
        return GeniusScraper.scrapeLyrics(geniusUrl);
    }

}