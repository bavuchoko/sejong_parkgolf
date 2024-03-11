package com.pjs.golf.config.token;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface TokenManager {

    String createToken(Authentication authentication, TokenType tokenType);
    Authentication getAuthenticationFromRefreshToken(HttpServletRequest request);
    boolean validateToken(String token);

    void destroyTokens(HttpServletRequest request);
    Authentication getAuthentication(String token);

    String getId(String accessToken);
    String getUsername(String accessToken);
    String getName(String accessToken);
    String getJoinDate(String accessToken);

    String getBirth(String accessToken);

    String getGender(String accessToken);
}