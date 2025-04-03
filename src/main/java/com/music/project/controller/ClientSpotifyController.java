package com.music.project.controller;

import com.music.project.service.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

import static com.music.project.util.SpotifyUtils.handleQueue;

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
        } else if("restart".equalsIgnoreCase(action)) {
            spotifyService.restart(accessToken, (String) session.getAttribute("track_uri"), session);
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
        } else if("queue".equalsIgnoreCase(action)) {
            Map queueJson = spotifyService.getQueue(accessToken);
            handleQueue(session, queueJson);
            return ResponseEntity.ok(queueJson);
        }
        return ResponseEntity.ok("Azione " + action + " eseguita con successo");
    }

    @GetMapping("/search/{action}")
    public ResponseEntity<?> searchSpotify(@RequestParam String query, @PathVariable String action, HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token non trovato");
        }
        if("ask".equalsIgnoreCase(action)) {
            String searchResultsJson = spotifyService.searchTrack(accessToken, query);
            return ResponseEntity.ok(searchResultsJson);
        } else if("play".equalsIgnoreCase(action)) {
            spotifyService.searchPlay(accessToken, query, session);
            return ResponseEntity.ok(Collections.singletonMap("message", "Azione play eseguita con successo"));
        }
        return ResponseEntity.ok("Azione " + action + " eseguita con successo");
    }
}
