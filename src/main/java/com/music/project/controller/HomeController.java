package com.music.project.controller;

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

    public HomeController(SpotifyService spotifyService, GeniusService geniusService) {
        this.spotifyService = spotifyService;
        this.geniusService = geniusService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("username", "Alberto");
        return "index";
    }

    @GetMapping("/play")
    public String play(@RequestParam("access_token") String accessToken, Model model) {
        model.addAttribute("accessToken", accessToken);
        Map response = spotifyService.getDevices(accessToken);
        List<Map<String, Object>> devices = (List<Map<String, Object>>) response.get("devices");
        if(devices != null && !devices.isEmpty()) {
            if((Boolean) devices.get(0).get("is_active")) {
                execSearch(spotifyService, geniusService, model, accessToken);
                return "play";
            } else {
                if(spotifyService.getPlayerState(accessToken, (String) devices.get(0).get("id"))) {
                    execSearch(spotifyService, geniusService, model, accessToken);
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
            spotifyService.pause(accessToken);
            String lyrics = geniusService.getLyrics(search);
            model.addAttribute("lyrics", lyrics);
        } else {
            model.addAttribute("message", "Nessun brano attualmente in riproduzione.");
        }
    }

}