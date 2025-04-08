package com.music.project.controller;

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
            return geminiService.callGeminiPrompt(lyrics);
        }
        return Mono.just("Testo non trovato!");
    }

}
