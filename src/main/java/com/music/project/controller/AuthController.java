package com.music.project.controller;

import com.music.project.service.SpotifyService;
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
    public String loginOk(@RequestParam("code") String code, Model model) {
        model.addAttribute("authCode", code);
        String accessToken = spotifyService.getSpotifyToken(code);
        if(accessToken == null) {
            model.addAttribute("message", "Problemi nel retrieve del token di accesso");
            return "error";
        }
        model.addAttribute("accessToken", accessToken);
        Map userInfo = spotifyService.getUserInfo(accessToken);
        model.addAttribute("display_name", userInfo.get("display_name"));
        return "login_ok";
    }
}