package com.music.project.service;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class SpotifyAuthService {

    private final WebClient webClient;

    public SpotifyAuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://accounts.spotify.com/api/token").build();
    }

    public String getSpotifyToken(String code) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Basic " + encodeClientCredentials("6b0c12d36953474181057af3eb7f3787", "90f2b1c24f0b4e4ba387cb9ff07bcf68"))
                .bodyValue("grant_type=authorization_code&code=" + code + "&redirect_uri=http://localhost:9999/callback")
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .block(); // Sincrono: ottiene direttamente il valore
    }

    private String encodeClientCredentials(String clientId, String clientSecret) {
        return java.util.Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    }
}