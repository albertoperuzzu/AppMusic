package com.music.project.interceptor;

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
            String accessToken = (String) session.getAttribute("accessToken");
            String refreshToken = (String) session.getAttribute("refreshToken");
            Long expiry = (Long) session.getAttribute("tokenExpiry");
            long currentTime = System.currentTimeMillis();
            if (accessToken != null && refreshToken != null && expiry != null && currentTime > expiry - 5 * 60 * 1000) {
                System.out.println("Access token is expiring soon. Refreshing...");
                Map<String, String> newTokens = spotifyService.refreshAccessToken(refreshToken);
                if (newTokens != null) {
                    session.setAttribute("accessToken", newTokens.get("access_token"));
                    session.setAttribute("tokenExpiry", currentTime + 3600 * 1000);
                    System.out.println("Access token refreshed successfully!");
                    if (newTokens.containsKey("refresh_token")) {
                        session.setAttribute("refreshToken", newTokens.get("refresh_token"));
                        System.out.println("Refresh token updated successfully!");
                    }
                } else {
                    System.err.println("Token refresh failed.");
                }
            }
        }
        return true;
    }
}
