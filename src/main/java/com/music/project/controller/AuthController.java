package com.music.project.controllers;

import com.music.project.services.SpotifyService;
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
            return "error";
        }
        model.addAttribute("accessToken", accessToken);
        Map userInfo = spotifyService.getUserInfo(accessToken);
        model.addAttribute("display_name", (String) userInfo.get("display_name"));
        return "login_ok";
    }
}