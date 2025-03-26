package com.music.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import com.music.project.services.SpotifyService;
import com.music.project.services.GeniusService;

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
        Map<String, Object> currentlyPlaying = spotifyService.getCurrentlyPlaying(accessToken);
        model.addAttribute("accessToken", accessToken);
        if (currentlyPlaying != null && currentlyPlaying.containsKey("item")) {
            Map<String, Object> item = (Map<String, Object>) currentlyPlaying.get("item");
            String trackName = (String) item.get("name");
            Map<String, Object> artist = ((List<Map<String, Object>>) item.get("artists")).get(0);
            String artistName = (String) artist.get("name");
            //Map<String, Object> album = ((List<Map<String, Object>>) item.get("album")).get(7);
            //String albumName = (String) album.get("name");
            model.addAttribute("track", trackName);
            model.addAttribute("artist", artistName);
            //model.addAttribute("album", albumName);
            String query = trackName + " " + artistName;
            String search = "https://genius.com" + geniusService.getLyricsLink(query);
            model.addAttribute("redirect", search);
        } else {
            model.addAttribute("message", "Nessun brano attualmente in riproduzione.");
            return "error";
        }
        return "play";
    }

}