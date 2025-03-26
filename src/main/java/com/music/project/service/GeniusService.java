package com.music.project.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.List;

@Service
public class GeniusService {

    private final WebClient webClient;

    public GeniusService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.genius.com").build();
    }

    public String getLyricsLink(String query) {
        try {
            // Chiamata API Genius e deserializzazione della risposta JSON in una Map
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search")
                            .queryParam("q", query)
                            .build())
                    .header("Authorization", "Bearer x_kPHnYUcA_KSej5FXco-_Wyxm3bhyXaX3AYvck4kzCWd8TKbza-H99CpSBkuu5b")
                    .retrieve()
                    .bodyToMono(Map.class)  // Deserializzazione della risposta in una Map
                    .block(); // Sincrono, otteniamo direttamente la risposta

            // Verifica che la risposta contenga i risultati
            Map<String, Object> responseResponse = (Map<String, Object>) response.get("response");
            if (responseResponse != null && responseResponse.containsKey("hits")) {
                // Prendi il primo risultato della lista "hits"
                var hits = (List<Map<String, Object>>) responseResponse.get("hits");
                if (hits != null && !hits.isEmpty()) {
                    // Estrai il "path" dal primo hit
                    Map<String, Object> result = (Map<String, Object>) hits.get(0).get("result");
                    return (String) result.get("path");  // Restituisci il path
                }
            }
            return null;  // Se non ci sono risultati
        } catch (WebClientResponseException e) {
            System.err.println("Errore nella risposta API: " + e.getMessage());
            return null;
        }
    }
}