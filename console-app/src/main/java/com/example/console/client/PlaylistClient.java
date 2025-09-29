package com.example.console.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PlaylistClient {

    private final RestTemplate restTemplate;

    @Value("${services.playlist.url}")
    private String playlistServiceUrl;

    public String createPlaylist(String name, String description, String token) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("name", name);
            request.put("description", description);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(playlistServiceUrl, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String getAllPlaylists() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(playlistServiceUrl, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String getMyPlaylists(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    playlistServiceUrl + "/my-playlists",
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String getPlaylistById(String id) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(playlistServiceUrl + "/" + id, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String getPlaylistWithSongs(String id) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    playlistServiceUrl + "/" + id + "/with-songs",
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String addSongToPlaylist(String playlistId, String songId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    playlistServiceUrl + "/" + playlistId + "/songs/" + songId,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String removeSongFromPlaylist(String playlistId, String songId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    playlistServiceUrl + "/" + playlistId + "/songs/" + songId,
                    HttpMethod.DELETE,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String updatePlaylist(String id, String name, String description, String token) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("name", name);
            request.put("description", description);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    playlistServiceUrl + "/" + id,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String deletePlaylist(String id, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    playlistServiceUrl + "/" + id,
                    HttpMethod.DELETE,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public String searchPlaylists(String name) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    playlistServiceUrl + "/search?name=" + name,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}