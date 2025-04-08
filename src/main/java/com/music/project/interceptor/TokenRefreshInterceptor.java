package com.music.project.interceptor;

import com.music.project.constant.AMConst;
import com.music.project.service.SpotifyService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Component
public class TokenRefreshInterceptor implements HandlerInterceptor {

    private final SpotifyService spotifyService;

    public TokenRefreshInterceptor(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String accessToken = (String) session.getAttribute(AMConst.SESSION_SPOTIFY_TOKEN);
            String refreshToken = (String) session.getAttribute(AMConst.SESSION_SPOTIFY_TOKEN_REFRESH);
            Long expiry = (Long) session.getAttribute(AMConst.SESSION_SPOTIFY_TOKEN_EXPIRY);
            long currentTime = System.currentTimeMillis();
            if (accessToken != null && refreshToken != null && expiry != null && currentTime > expiry - 5 * 60 * 1000) {
                System.out.println("Access token is expiring soon! Refreshing");
                Map<String, String> newTokens = spotifyService.refreshAccessToken(refreshToken);
                if (newTokens != null) {
                    session.setAttribute(AMConst.SESSION_SPOTIFY_TOKEN, newTokens.get(AMConst.JSON_SPOTIFY_ACCESS_TOKEN));
                    session.setAttribute(AMConst.SESSION_SPOTIFY_TOKEN_EXPIRY, currentTime + 3600 * 1000);
                    System.out.println("Access token refreshed successfully");
                    if (newTokens.containsKey(AMConst.JSON_SPOTIFY_REFRESH_TOKEN)) {
                        session.setAttribute(AMConst.SESSION_SPOTIFY_TOKEN_REFRESH, newTokens.get(AMConst.JSON_SPOTIFY_REFRESH_TOKEN));
                        System.out.println("Refresh token updated successfully");
                    }
                } else {
                    System.err.println("Token refresh failed");
                }
            }
        }
        return true;
    }
}
