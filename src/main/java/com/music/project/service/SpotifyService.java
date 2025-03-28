package com.music.project.service;

import com.music.project.config.SpotifyConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.http.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                .onStatus(HttpStatusCode::isError, clientResponse ->
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
        try {
            return WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .get()
                .uri("/me")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        } catch(Exception ex) {
            System.err.println("Errore nel recupero delle user info: " + ex.getMessage());
            return null;
        }
    }

    public Boolean getPlayerState(String accessToken, String deviceId) {
        try {
            // Add a delay to let Spotify App to open and be ready.
            // This call is not executed in the case the app is already open and active
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interruzione del delay: " + e.getMessage());
        }
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("device_ids", Collections.singletonList(deviceId));
            HttpStatus status = (HttpStatus) webClient.put()
                .uri("https://api.spotify.com/v1/me/player")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .toBodilessEntity()
                .map(ResponseEntity::getStatusCode)
                .block();  // Converte il Mono in un valore sincrono
            return status != null && status.is2xxSuccessful();
        } catch (Exception ex) {
            System.err.println("Errore durante transfer playback: " + ex.getMessage());
            return false;
        }
    }

    public Map getCurrentlyPlaying(String accessToken) {
        try {
            return webClient.get()
                .uri("https://api.spotify.com/v1/me/player/currently-playing")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Errore API: " + errorBody))))
                .bodyToMono(Map.class)
                .block();
        } catch (Exception ex) {
            System.err.println("Errore nel recupero del brano in riproduzione: " + ex.getMessage());
            return null;
        }
    }

    public void play(String accessToken) {
        try {
            WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .put()
                .uri("/me/player/play")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch(Exception ex) {
            System.err.println("Errore nella riproduzione del brano: " + ex.getMessage());
        }
    }

    public void pause(String accessToken) {
        try {
            WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .put()
                .uri("/me/player/pause")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch(Exception ex) {
            System.err.println("Errore nella messa in pausa del brano: " + ex.getMessage());
        }

    }

    public void previousTrack(String accessToken) {
        try {
            webClient.post()
                .uri("https://api.spotify.com/v1/me/player/previous")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity()
                .block(); // Attendi la risposta
        } catch (WebClientResponseException e) {
            System.err.println("Errore nella richiesta previous track: " + e.getMessage());
        }
    }

    public void nextTrack(String accessToken) {
        try {
            webClient.post()
                .uri("https://api.spotify.com/v1/me/player/next")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity()
                .block();
        } catch (WebClientResponseException e) {
            System.err.println("Errore nella richiesta next track: " + e.getMessage());
        }
    }

    public Map getDevices(String accessToken) {
        try {
            return WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .get()
                .uri("/me/player/devices")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        } catch(Exception ex) {
            System.err.println("Errore nel recupero dei device attivi: " + ex.getMessage());
            return null;
        }
    }

    private String encodeClientCredentials(String clientId, String clientSecret) {
        return java.util.Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    }
}