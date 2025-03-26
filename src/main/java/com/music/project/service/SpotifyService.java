package com.music.project.services;

import com.music.project.config.SpotifyConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import org.springframework.http.*;
import java.util.Map;

@Service
public class SpotifyService {

    private final WebClient webClient;
    private final SpotifyConfig spotifyConfig;

    public SpotifyService(SpotifyConfig spotifyConfig, WebClient.Builder webClientBuilder) {
        this.spotifyConfig = spotifyConfig;
        this.webClient = webClientBuilder.baseUrl("https://accounts.spotify.com/api").build();
    }

    public String getSpotifyToken(String code) {
        try {
            Map response = webClient.post()
                    .uri("/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header("Authorization", "Basic " + encodeClientCredentials("6b0c12d36953474181057af3eb7f3787", "90f2b1c24f0b4e4ba387cb9ff07bcf68"))
                    .bodyValue("grant_type=authorization_code&code=" + code + "&redirect_uri=http://localhost:9999/callback")
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("Error: " + errorBody))))
                    .bodyToMono(Map.class)
                    .block();
            return (String) response.get("access_token");
        } catch (Exception ex) {
            System.err.println("Errore nel ricevimento del token di auth: " + ex.getMessage());
            return null;
        }
    }

    // Esempio: ottieni informazioni dell'utente
    public Map getUserInfo(String accessToken) {
        return WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .get()
                .uri("/me")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map<String, Object> getCurrentlyPlaying(String accessToken) {
        try {
            return webClient.get()
                    .uri("https://api.spotify.com/v1/me/player/currently-playing")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("Errore API: " + errorBody))))
                    .bodyToMono(Map.class)
                    .block(); // Sincrono, ottiene direttamente il valore
        } catch (Exception ex) {
            System.err.println("Errore nel recupero del brano in riproduzione: " + ex.getMessage());
            return null;
        }
    }

    // Esempio: invia comando di play al player
    public void play(String accessToken) {
        WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .put()
                .uri("/me/player/play")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private String encodeClientCredentials(String clientId, String clientSecret) {
        return java.util.Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    }
}