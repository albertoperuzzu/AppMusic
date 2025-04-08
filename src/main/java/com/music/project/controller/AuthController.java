package com.music.project.controller;

import com.music.project.constant.AMConst;
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
            model.addAttribute(AMConst.MODEL_MESSAGE, "Problemi nel retrieve del token di accesso");
            return AMConst.ERROR_PAGE;
        }
        String accessToken = tokenResponse.get(AMConst.JSON_SPOTIFY_ACCESS_TOKEN);
        session.setAttribute(AMConst.SESSION_SPOTIFY_TOKEN, accessToken);
        long currentTime = System.currentTimeMillis();
        session.setAttribute(AMConst.SESSION_SPOTIFY_TOKEN_EXPIRY, currentTime + 3600 * 1000);
        if(tokenResponse.containsKey(AMConst.JSON_SPOTIFY_REFRESH_TOKEN)) {
            String refreshToken = tokenResponse.get(AMConst.JSON_SPOTIFY_REFRESH_TOKEN);
            session.setAttribute(AMConst.SESSION_SPOTIFY_TOKEN_REFRESH, refreshToken);
        }
        Map userInfo = spotifyService.getUserInfo(accessToken);
        model.addAttribute(AMConst.MODEL_USERNAME, userInfo.get(AMConst.JSON_SPOTIFY_USERNAME));
        session.setAttribute(AMConst.SESSION_SPOTIFY_USERNAME, userInfo.get(AMConst.JSON_SPOTIFY_USERNAME));
        return AMConst.LOGIN_OK_PAGE;
    }
}