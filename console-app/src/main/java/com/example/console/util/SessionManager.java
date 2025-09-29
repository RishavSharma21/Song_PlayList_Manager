package com.example.console.util;

import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    private String jwtToken;
    private String username;

    public void setSession(String token, String username) {
        this.jwtToken = token;
        this.username = username;
    }

    public String getToken() {
        return jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggedIn() {
        return jwtToken != null && !jwtToken.isEmpty();
    }

    public void clearSession() {
        this.jwtToken = null;
        this.username = null;
    }
}