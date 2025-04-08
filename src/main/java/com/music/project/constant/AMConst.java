package com.music.project.constant;

public final class AMConst {

    // PAGES
    public static final String PLAY_PAGE = "play";
    public static final String PODCAST_PAGE = "podcast";
    public static final String INDEX_PAGE = "index";
    public static final String ERROR_PAGE = "error";
    public static final String LANDING_PAGE = "landing";
    public static final String CALLBACK_PAGE = "callback";
    public static final String LOGIN_OK_PAGE = "login_ok";

    // MODEL ATTRIBUTES
    public static final String MODEL_MESSAGE = "message";
    public static final String MODEL_USERNAME = "display_name";
    public static final String MODEL_CLIENT_ID = "client_id";
    public static final String MODEL_REQUEST_URI = "request_uri";
    public static final String MODEL_TRACK_DURATION = "track_duration_ms";
    public static final String MODEL_ARTIST = "artist";
    public static final String MODEL_TRACK = "track";
    public static final String MODEL_REDIRECT = "redirect";
    public static final String MODEL_LYRICS = "lyrics";

    // SESSION ATTRIBUTES
    public static final String SESSION_SPOTIFY_TOKEN = "accessToken";
    public static final String SESSION_SPOTIFY_TOKEN_EXPIRY = "tokenExpiry";
    public static final String SESSION_SPOTIFY_TOKEN_REFRESH = "refreshToken";
    public static final String SESSION_SPOTIFY_USERNAME = "display_name";
    public static final String SESSION_SPOTIFY_DEVICE_ID = "deviceId";
    public static final String SESSION_SPOTIFY_DURATION = "duration_ms";
    public static final String SESSION_SPOTIFY_TRACK_URI = "track_uri";
    public static final String SESSION_SPOTIFY_CURRENT_LYRICS = "current_lyrics";
    public static final String SESSION_SPOTIFY_QUEUE = "queue";

    // SPOTIFY ACTIONS
    public static final String SPOTIFY_PLAY = "play";
    public static final String SPOTIFY_PAUSE = "pause";
    public static final String SPOTIFY_RESTART = "restart";
    public static final String SPOTIFY_NEXT = "next";
    public static final String SPOTIFY_PREVIOUS = "previous";
    public static final String SPOTIFY_DEVICES = "devices";
    public static final String SPOTIFY_QUEUE = "queue";
    public static final String SPOTIFY_ASK = "ask";

    // GEMINI PROMPTS
    public static final String GEMINI_TEXT_PROMPT = "Traduci in italiano questa canzone (solo traduzione nel testo): ";

    // SPOTIFY JSONS
    public static final String JSON_SPOTIFY_USERNAME = "display_name";
    public static final String JSON_SPOTIFY_ACCESS_TOKEN = "access_token";
    public static final String JSON_SPOTIFY_REFRESH_TOKEN = "refresh_token";
    public static final String JSON_SPOTIFY_ID = "id";
    public static final String JSON_SPOTIFY_ACTIVE = "is_active";
    public static final String JSON_SPOTIFY_DEVICES = "devices";
    public static final String JSON_SPOTIFY_DEVICE_IDS = "device_ids";
    public static final String JSON_SPOTIFY_QUEUE = "queue";
    public static final String JSON_SPOTIFY_URI = "uri";
    public static final String JSON_SPOTIFY_ITEM = "item";
    public static final String JSON_SPOTIFY_NAME = "name";
    public static final String JSON_SPOTIFY_ARTISTS = "artists";
    public static final String JSON_SPOTIFY_DURATION = "duration_ms";


    // GENIUS JSONS
    public static final String JSON_GENIUS_RESPONSE = "response";
    public static final String JSON_GENIUS_HITS = "hits";
    public static final String JSON_GENIUS_RESULT = "result";
    public static final String JSON_GENIUS_PATH = "path";

    // PATTERNS
    public static final String PATTERN_SPOTIFY_CLIENT = "/client/spotify/**";
    public static final String PATTERN_SPOTIFY_EXTERN = "https://accounts.spotify.com/**";
    public static final String PATTERN_GENIUS_BASEURL = "https://api.genius.com";
    public static final String PATTERN_SPOTIFY_BASEURL_API = "https://accounts.spotify.com/api";
    public static final String PATTERN_SPOTIFY_BASEURL_V1 = "https://api.spotify.com/v1";
    public static final String PATTERN_SPOTIFY_BASEURL_PLAYER = "https://api.spotify.com/v1/me/player";

}
