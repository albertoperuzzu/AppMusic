package com.music.project.service;

import com.music.project.config.GeniusConfig;
import com.music.project.constant.AMConst;
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
        this.webClient = webClientBuilder.baseUrl(AMConst.PATTERN_GENIUS_BASEURL).build();
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
            Map<String, Object> responseResponse = (Map<String, Object>) response.get(AMConst.JSON_GENIUS_RESPONSE);
            if (responseResponse != null && responseResponse.containsKey(AMConst.JSON_GENIUS_HITS)) {
                var hits = (List<Map<String, Object>>) responseResponse.get(AMConst.JSON_GENIUS_HITS);
                if (hits != null && !hits.isEmpty()) {
                    Map<String, Object> result = (Map<String, Object>) hits.get(0).get(AMConst.JSON_GENIUS_RESULT);
                    return (String) result.get(AMConst.JSON_GENIUS_PATH);
                }
            }
            return null;
        } catch (WebClientResponseException e) {
            System.err.println("Errore nella risposta GENIUS API: " + e.getMessage());
            return null;
        }
    }

    public String getLyrics(String geniusUrl) {
        return GeniusScraper.scrapeLyrics(geniusUrl);
    }

}