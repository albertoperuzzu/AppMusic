package com.music.project.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Service
public class GoogleService {

    private final WebClient webClient;

    public GoogleService(WebClient.Builder webClientBuilder) {
        ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build();
        this.webClient = webClientBuilder.baseUrl("https://texttospeech.googleapis.com/v1")
                .exchangeStrategies(strategies).clientConnector(new ReactorClientHttpConnector(HttpClient.create())).build();
    }

    public GoogleCredentials getGoogleCredentials() throws IOException {
        String credentialsPath = "src/main/resources/credentials/googleService.json";
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(credentialsPath))
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        credentials.refreshIfExpired();
        return credentials;
    }

    public String getAccessToken() throws IOException {
        GoogleCredentials credentials = getGoogleCredentials();
        return credentials.getAccessToken().getTokenValue();
    }

    public Mono<byte[]> synthesizeSpeech(String text) {
        String requestBody = """
        {
          "input": {
            "text": "%s"
          },
          "voice": {
            "languageCode": "it-IT",
            "ssmlGender": "FEMALE"
          },
          "audioConfig": {
            "audioEncoding": "MP3"
          }
        }
        """.formatted(text);
        try {
            return webClient.post()
                    .uri("/text:synthesize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(response -> {
                        try {
                            String audioContent = com.fasterxml.jackson.databind.json.JsonMapper.builder()
                                .build()
                                .readTree(response)
                                .get("audioContent")
                                .asText();
                            byte[] audioBytes = java.util.Base64.getDecoder().decode(audioContent);
                            return Mono.just(audioBytes);
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("Errore nel parsing della risposta TTS", e));
                        }
                    });
        } catch (WebClientResponseException e) {
            System.err.println("Errore nella richiesta TTS: " + e.getMessage());
            return Mono.error(new RuntimeException("Errore nella richiesta TTS", e));
        } catch (IOException e) {
            System.err.println("Errore nella richiesta del token: " + e.getMessage());
            return Mono.error(new RuntimeException("Errore nella richiesta TTS", e));
        }
    }

}