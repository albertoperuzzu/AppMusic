package com.music.project.util;

import com.music.project.constant.AMConst;
import com.music.project.service.GeniusService;
import com.music.project.service.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpotifyUtils {

    public static Boolean execSearch(SpotifyService spotifyService, GeniusService geniusService, Model model, String accessToken, HttpSession session) {
        Map currentlyPlaying = spotifyService.getCurrentlyPlaying(accessToken);
        if (currentlyPlaying != null && currentlyPlaying.containsKey(AMConst.JSON_SPOTIFY_ITEM)) {
            if(currentlyPlaying.get(AMConst.JSON_SPOTIFY_ITEM) == null) {
                model.addAttribute(AMConst.MODEL_MESSAGE, "Podcast in riproduzione! Seleziona una canzone!");
                return false;
            } else {
                Map<String, Object> item = (Map) currentlyPlaying.get(AMConst.JSON_SPOTIFY_ITEM);
                String trackName = (String) item.get(AMConst.JSON_SPOTIFY_NAME);
                Map<String, Object> artist = ((List<Map>) item.get(AMConst.JSON_SPOTIFY_ARTISTS)).get(0);
                String artistName = (String) artist.get(AMConst.JSON_SPOTIFY_NAME);
                model.addAttribute(AMConst.MODEL_TRACK, trackName);
                model.addAttribute(AMConst.MODEL_ARTIST, artistName);
                session.setAttribute(AMConst.SESSION_SPOTIFY_DURATION, item.get(AMConst.JSON_SPOTIFY_DURATION));
                session.setAttribute(AMConst.SESSION_SPOTIFY_TRACK_URI, item.get(AMConst.JSON_SPOTIFY_URI));
                String query = trackName + " " + artistName;
                query = query.replace(" - Remastered", "").replace(" - live version", "").replace(" - Live", "");
                String search = "https://genius.com" + geniusService.getLyricsLink(query);
                model.addAttribute(AMConst.MODEL_REDIRECT, search);
                String lyrics = geniusService.getLyrics(search);
                lyrics = lyrics.replaceFirst("^<br><br>", "");
                model.addAttribute(AMConst.MODEL_LYRICS, lyrics);
                session.setAttribute(AMConst.SESSION_SPOTIFY_CURRENT_LYRICS, lyrics);
                return true;
            }
        } else {
            model.addAttribute(AMConst.MODEL_MESSAGE, "Nessun brano attualmente in riproduzione.");
            return false;
        }
    }

    public static void handleQueue(HttpSession session, Map queueJson) {
        if (queueJson != null) {
            Object queueObj = queueJson.get(AMConst.JSON_SPOTIFY_QUEUE);
            List<Map<String, Object>> queueList = (List<Map<String, Object>>) queueObj;
            if(queueList != null && !queueList.isEmpty()) {
                List<String> trackList = new ArrayList<>();
                for (Map<String, Object> stringObjectMap : queueList) {
                    String track = (String) stringObjectMap.get(AMConst.JSON_SPOTIFY_URI);
                    trackList.add(track);
                }
                if(!trackList.isEmpty()) {
                    session.setAttribute(AMConst.SESSION_SPOTIFY_QUEUE, trackList);
                }
            }
        }
    }

}
