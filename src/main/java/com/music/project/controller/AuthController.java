package com.music.project.controller;

import com.music.project.service.SpotifyAuthService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final SpotifyAuthService spotifyAuthService;

    public AuthController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    @GetMapping("/callback")
    public String loginOk(@RequestParam("code") String code, Model model) {
        String accessToken = spotifyAuthService.getSpotifyToken(code);
        model.addAttribute("authCode", code);
        model.addAttribute("accessToken", accessToken);
        return "login_ok"; // Thymeleaf caricherà login_ok.html
    }
}