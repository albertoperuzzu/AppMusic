package com.music.project.service;

import com.music.project.config.SpotifyConfig;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.http.*;
import java.util.*;

@Service
public class SpotifyService {

    private final WebClient webClient;
    private final SpotifyConfig spotifyConfig;

    public SpotifyService(SpotifyConfig spotifyConfig, WebClient.Builder webClientBuilder) {
        this.spotifyConfig = spotifyConfig;
        this.webClient = webClientBuilder.baseUrl("https://accounts.spotify.com/api").build();
    }

    public Map<String, String> getSpotifyToken(String code) {
        try {
            Map<String, Object> response = webClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Basic " + encodeClientCredentials(spotifyConfig.getId(), spotifyConfig.getSecret()))
                .bodyValue("grant_type=authorization_code&code=" + code + "&redirect_uri=" + spotifyConfig.getUri())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error: " + errorBody))))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
            if (response != null) {
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", response.get("access_token").toString());
                if(response.containsKey("refresh_token")) {
                    tokens.put("refresh_token", response.get("refresh_token").toString());
                }
                return tokens;
            }
            return null;
        } catch (Exception ex) {
            System.err.println("Errore nel ricevimento del token di auth: " + ex.getMessage());
            return null;
        }
    }

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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interruzione del delay: " + e.getMessage());
        }
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("device_ids", Collections.singletonList(deviceId));
            //requestBody.put("play", true);
            HttpStatus status = (HttpStatus) webClient.put()
                .uri("https://api.spotify.com/v1/me/player")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .toBodilessEntity()
                .map(ResponseEntity::getStatusCode)
                .block();
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

    public String getDevicesClient(String accessToken) {
        try {
            return WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .get()
                .uri("/me/player/devices")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        } catch (Exception ex) {
            System.err.println("Errore nella richiesta devices: " + ex.getMessage());
            return null;
        }
    }

    public void restart(String accessToken, String trackURI, HttpSession session) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            List<String> uris = new ArrayList<>();
            uris.add(trackURI);
            if(session.getAttribute("queue") != null) {
                List<String> queue = (List<String>) session.getAttribute("queue");
                uris.addAll(queue);
            }
            requestBody.put("uris", uris);
            requestBody.put("position_ms", 0);
            WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
                .put()
                .uri("/me/player/play")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch(Exception ex) {
            System.err.println("Errore nel restart del brano: " + ex.getMessage());
        }
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        String credentials = spotifyConfig.getId() + ":" + spotifyConfig.getSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        try {
            Map<String, Object> response = webClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
            if (response != null && response.containsKey("access_token")) {
                Map<String, String> tokenData = new HashMap<>();
                tokenData.put("access_token", response.get("access_token").toString());
                if (response.containsKey("refresh_token")) {
                    tokenData.put("refresh_token", response.get("refresh_token").toString());
                }
                return tokenData;
            }
        } catch (Exception e) {
            System.err.println("Error refreshing token: " + e.getMessage());
        }
        return null;
    }

    public Map getQueue(String accessToken) {
        try {
            return WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .get()
                .uri("/me/player/queue")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        } catch(Exception ex) {
            System.err.println("Errore nel recupero della queue: " + ex.getMessage());
            return null;
        }
    }

    public String searchTrack(String accessToken, String query) {
        String queryString = "/search" + "?q=" + query + "&type=track&limit=10";
        try {
            return WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .get()
                .uri(queryString)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        } catch(Exception ex) {
            System.err.println("Errore nel recupero della queue: " + ex.getMessage());
            return null;
        }
    }

    public void searchPlay(String accessToken, String query, HttpSession session) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            List<String> uris = new ArrayList<>();
            uris.add(query);
            if(session.getAttribute("queue") != null) {
                List<String> queue = (List<String>) session.getAttribute("queue");
                uris.addAll(queue);
            }
            requestBody.put("uris", uris);
            requestBody.put("position_ms", 0);
            WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
                .put()
                .uri("/me/player/play")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch(Exception ex) {
            System.err.println("Errore nel restart del brano: " + ex.getMessage());
        }
    }

    private String encodeClientCredentials(String clientId, String clientSecret) {
        return java.util.Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    }
}