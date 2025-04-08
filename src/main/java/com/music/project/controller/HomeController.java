package com.music.project.controller;

import com.music.project.config.SpotifyConfig;
import com.music.project.constant.AMConst;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import com.music.project.service.SpotifyService;
import com.music.project.service.GeniusService;

import static com.music.project.util.SpotifyUtils.execSearch;

@Controller
public class HomeController {

    private final SpotifyService spotifyService;
    private final GeniusService geniusService;
    private final SpotifyConfig spotifyConfig;

    public HomeController(SpotifyService spotifyService, GeniusService geniusService, SpotifyConfig spotifyConfig) {
        this.spotifyService = spotifyService;
        this.geniusService = geniusService;
        this.spotifyConfig = spotifyConfig;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute(AMConst.MODEL_CLIENT_ID, spotifyConfig.getId());
        model.addAttribute(AMConst.MODEL_REQUEST_URI, spotifyConfig.getUri());
        return AMConst.INDEX_PAGE;
    }

    @GetMapping("/landing")
    public String landing(Model model, HttpSession session) {
        String accessToken = (String) session.getAttribute(AMConst.SESSION_SPOTIFY_TOKEN);
        Map response = spotifyService.getDevices(accessToken);
        List<Map<String, Object>> devices = (List<Map<String, Object>>) response.get(AMConst.JSON_SPOTIFY_DEVICES);
        model.addAttribute(AMConst.MODEL_USERNAME, session.getAttribute(AMConst.SESSION_SPOTIFY_USERNAME));
        if(devices != null && !devices.isEmpty()) {
            if((Boolean) devices.get(0).get(AMConst.JSON_SPOTIFY_ACTIVE)) {
                session.setAttribute(AMConst.SESSION_SPOTIFY_DEVICE_ID, devices.get(0).get(AMConst.JSON_SPOTIFY_ID));
                return AMConst.LANDING_PAGE;
            } else {
                if(spotifyService.getPlayerState(accessToken, (String) devices.get(0).get(AMConst.JSON_SPOTIFY_ID))) {
                    session.setAttribute(AMConst.SESSION_SPOTIFY_DEVICE_ID, devices.get(0).get(AMConst.JSON_SPOTIFY_ID));
                    return AMConst.LANDING_PAGE;
                } else {
                    model.addAttribute(AMConst.MODEL_MESSAGE, "Nessun dispositivo attivo sull'account");
                    return AMConst.ERROR_PAGE;
                }
            }
        } else {
            model.addAttribute(AMConst.MODEL_MESSAGE, "Nessun dispositivo attivo sull'account");
            return AMConst.ERROR_PAGE;
        }
    }

    @GetMapping("/play")
    public String play(Model model, HttpSession session) {
        String accessToken = (String) session.getAttribute(AMConst.SESSION_SPOTIFY_TOKEN);
        if(session.getAttribute(AMConst.SESSION_SPOTIFY_DEVICE_ID) != null) {
            if(execSearch(spotifyService, geniusService, model, accessToken, session)) {
                //spotifyService.restart(accessToken, (String) session.getAttribute("track_uri"), session);
                model.addAttribute(AMConst.MODEL_TRACK_DURATION, session.getAttribute(AMConst.SESSION_SPOTIFY_DURATION));
                return AMConst.PLAY_PAGE;
            } else {
                return AMConst.PODCAST_PAGE;
            }
        } else {
            return AMConst.ERROR_PAGE;
        }
    }

}