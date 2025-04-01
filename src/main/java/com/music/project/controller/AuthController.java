package com.music.project.controller;

import com.music.project.service.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class AuthController {

    private final SpotifyService spotifyService;

    public AuthController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/callback")
    public String loginOk(@RequestParam("code") String code, Model model, HttpSession session) {
        Map<String, String> tokenResponse = spotifyService.getSpotifyToken(code);
        if(tokenResponse == null) {
            model.addAttribute("message", "Problemi nel retrieve del token di accesso");
            return "error";
        }
        String accessToken = tokenResponse.get("access_token");
        session.setAttribute("accessToken", accessToken);
        long currentTime = System.currentTimeMillis();
        session.setAttribute("tokenExpiry", currentTime + 3600 * 1000);
        if(tokenResponse.containsKey("refresh_token")) {
            String refreshToken = tokenResponse.get("refresh_token");
            session.setAttribute("refreshToken", refreshToken);
        }
        Map userInfo = spotifyService.getUserInfo(accessToken);
        model.addAttribute("display_name", userInfo.get("display_name"));
        session.setAttribute("display_name", userInfo.get("display_name"));
        return "login_ok";
    }
}