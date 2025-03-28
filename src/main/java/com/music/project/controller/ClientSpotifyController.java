package com.music.project.controller;

import com.music.project.service.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/spotify")
public class ClientSpotifyController {

    private final SpotifyService spotifyService;

    public ClientSpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @PutMapping("/put/{action}")
    public ResponseEntity<?> handlePutAction(@PathVariable String action, HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token non trovato");
        }
        if ("play".equalsIgnoreCase(action)) {
            spotifyService.play(accessToken);
        } else if ("pause".equalsIgnoreCase(action)) {
            spotifyService.pause(accessToken);
        }
        return ResponseEntity.ok("Azione " + action + " eseguita con successo");
    }

    @PostMapping("/post/{action}")
    public ResponseEntity<?> handlePostAction(@PathVariable String action, HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token non trovato");
        }
        if ("next".equalsIgnoreCase(action)) {
            spotifyService.nextTrack(accessToken);
        } else if ("previous".equalsIgnoreCase(action)) {
            spotifyService.previousTrack(accessToken);
        }
        return ResponseEntity.ok("Azione " + action + " eseguita con successo");
    }

    @GetMapping("/get/{action}")
    public ResponseEntity<?> handleGetAction(@PathVariable String action, HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token non trovato");
        }
        if("devices".equalsIgnoreCase(action)) {
            String devicesJson = spotifyService.getDevicesClient(accessToken);
            return ResponseEntity.ok(devicesJson);
        }
        return ResponseEntity.ok("Azione " + action + " eseguita con successo");
    }
}
