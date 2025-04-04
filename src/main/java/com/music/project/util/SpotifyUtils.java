package com.music.project.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.project.service.GeniusService;
import com.music.project.service.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpotifyUtils {

    public static void execSearch(SpotifyService spotifyService, GeniusService geniusService, Model model, String accessToken, HttpSession session) {
        Map currentlyPlaying = spotifyService.getCurrentlyPlaying(accessToken);
        if (currentlyPlaying != null && currentlyPlaying.containsKey("item")) {
            if(currentlyPlaying.get("item") == null) {
                model.addAttribute("message", "Podcast in riproduzione! Seleziona una canzone!");
            }
            Map<String, Object> item = (Map) currentlyPlaying.get("item");
            String trackName = (String) item.get("name");
            Map<String, Object> artist = ((List<Map>) item.get("artists")).get(0);
            String artistName = (String) artist.get("name");
            model.addAttribute("track", trackName);
            model.addAttribute("artist", artistName);
            session.setAttribute("track_uri", item.get("uri"));
            String query = trackName + " " + artistName;
            query = query.replace(" - Remastered", "").replace(" - live version", "").replace(" - Live", "");
            String search = "https://genius.com" + geniusService.getLyricsLink(query);
            model.addAttribute("redirect", search);
            //spotifyService.pause(accessToken);
            String lyrics = geniusService.getLyrics(search);
            lyrics = lyrics.replaceFirst("^<br><br>", "");
            model.addAttribute("lyrics", lyrics);
            session.setAttribute("current_lyrics", lyrics);
        } else {
            model.addAttribute("message", "Nessun brano attualmente in riproduzione.");
        }
    }

    public static void handleQueue(HttpSession session, Map queueJson) {
        if (queueJson != null) {
            Object queueObj = queueJson.get("queue");
            List<Map<String, Object>> queueList = (List<Map<String, Object>>) queueObj;
            if(queueList != null && !queueList.isEmpty()) {
                List<String> trackList = new ArrayList<>();
                for (Map<String, Object> stringObjectMap : queueList) {
                    String track = (String) stringObjectMap.get("uri");
                    trackList.add(track);
                }
                if(!trackList.isEmpty()) {
                    session.setAttribute("queue", trackList);
                }
            }
        }
    }

}
