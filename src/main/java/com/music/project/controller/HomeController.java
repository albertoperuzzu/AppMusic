package com.music.project.controller;

import com.music.project.config.SpotifyConfig;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import com.music.project.service.SpotifyService;
import com.music.project.service.GeniusService;

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
        model.addAttribute("client_id", spotifyConfig.getId());
        model.addAttribute("request_uri", spotifyConfig.getUri());
        return "index";
    }

    @GetMapping("/play")
    public String play(Model model, HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        model.addAttribute("accessToken", accessToken);
        Map response = spotifyService.getDevices(accessToken);
        List<Map<String, Object>> devices = (List<Map<String, Object>>) response.get("devices");
        if(devices != null && !devices.isEmpty()) {
            if((Boolean) devices.get(0).get("is_active")) {
                execSearch(spotifyService, geniusService, model, accessToken);
                session.setAttribute("deviceId", devices.get(0).get("id"));
                return "play";
            } else {
                if(spotifyService.getPlayerState(accessToken, (String) devices.get(0).get("id"))) {
                    execSearch(spotifyService, geniusService, model, accessToken);
                    session.setAttribute("deviceId", devices.get(0).get("id"));
                    return "play";
                } else {
                    model.addAttribute("message", "Nessun dispositivo attivo sull'account");
                    return "error";
                }
            }
        } else {
            model.addAttribute("message", "Nessun dispositivo attivo sull'account");
            return "error";
        }
    }

    private void execSearch(SpotifyService spotifyService, GeniusService geniusService, Model model, String accessToken) {
        try {
            Thread.sleep(300);
            Map currentlyPlaying = spotifyService.getCurrentlyPlaying(accessToken);
            if (currentlyPlaying != null && currentlyPlaying.containsKey("item")) {
                Map<String, Object> item = (Map) currentlyPlaying.get("item");
                String trackName = (String) item.get("name");
                Map<String, Object> artist = ((List<Map>) item.get("artists")).get(0);
                String artistName = (String) artist.get("name");
                model.addAttribute("track", trackName);
                model.addAttribute("artist", artistName);
                String query = trackName + " " + artistName;
                String search = "https://genius.com" + geniusService.getLyricsLink(query);
                model.addAttribute("redirect", search);
                //spotifyService.pause(accessToken);
                String lyrics = geniusService.getLyrics(search);
                model.addAttribute("lyrics", lyrics);
            } else {
                model.addAttribute("message", "Nessun brano attualmente in riproduzione.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interruzione del delay: " + e.getMessage());
        }
    }

}