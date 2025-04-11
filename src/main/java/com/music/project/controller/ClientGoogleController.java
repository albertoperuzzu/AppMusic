package com.music.project.controller;

import com.music.project.service.GoogleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/google")
public class ClientGoogleController {

    private final GoogleService googleService;

    public ClientGoogleController(GoogleService googleService) {
        this.googleService = googleService;
    }

    @PostMapping("/get_speech")
    public Mono<ResponseEntity<byte[]>> getSpeech(HttpSession session) {
        String text = (String) session.getAttribute("speech_text");
        if (text == null || text.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }
        Mono<byte[]> audio = googleService.synthesizeSpeech(text);
        return audio.map(audioBytes -> ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.mp3\"")
            .contentType(MediaType.valueOf("audio/mpeg"))
            .body(audioBytes));
    }

}