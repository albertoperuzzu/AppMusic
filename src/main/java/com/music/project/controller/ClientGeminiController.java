package com.music.project.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.project.constant.AMConst;
import com.music.project.service.GeminiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/AI")
public class ClientGeminiController {

    private final GeminiService geminiService;

    public ClientGeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/get_translation")
    public Mono<String> generatePrompt(HttpSession session) {
        String lyrics = (String) session.getAttribute(AMConst.SESSION_SPOTIFY_CURRENT_LYRICS);
        if(lyrics != null) {
            Mono<String> mono = geminiService.callGeminiPrompt(lyrics);
            String jsonResponse = mono.block();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonResponse);
                String extractedText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                extractedText = extractedText.replaceAll("\"", "'").replaceAll("\\*\\*", "").replaceAll("\\*", "");
                session.setAttribute("speech_text", extractedText);
            } catch (Exception e) {
                System.err.println("Errore nel parsing del JSON di Gemini: " + e.getMessage());
                session.setAttribute("speech_text", null);
            }
            return mono;
        }
        return Mono.just("Testo non trovato!");
    }

}
