package com.example.console.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SongClient {

    private final RestTemplate restTemplate;

    @Value("${services.song.url}")
    private String songServiceUrl;

    public String addSong(String title, String artist, String album, String genre, int duration, String token) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("title", title);
            request.put("artist", artist);
            request.put("album", album);
            request.put("genre", genre);
            request.put("duration", duration);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(songServiceUrl, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "{\"error\": \"" + cleanErrorMessage(e.getResponseBodyAsString()) + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Connection error: " + e.getMessage() + "\"}";
        }
    }

    public String getAllSongs() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(songServiceUrl, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "{\"error\": \"" + cleanErrorMessage(e.getResponseBodyAsString()) + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Connection error: " + e.getMessage() + "\"}";
        }
    }

    public String getSongById(String id) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(songServiceUrl + "/" + id, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "{\"error\": \"" + cleanErrorMessage(e.getResponseBodyAsString()) + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Connection error: " + e.getMessage() + "\"}";
        }
    }

    public String searchSongs(String searchType, String searchValue) {
        try {
            String url = songServiceUrl + "/search?" + searchType + "=" + searchValue;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "{\"error\": \"" + cleanErrorMessage(e.getResponseBodyAsString()) + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Connection error: " + e.getMessage() + "\"}";
        }
    }

    public String getMySongs(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    songServiceUrl + "/my-songs",
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "{\"error\": \"" + cleanErrorMessage(e.getResponseBodyAsString()) + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Connection error: " + e.getMessage() + "\"}";
        }
    }

    public String updateSong(String id, String title, String artist, String album, String genre, int duration, String token) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("title", title);
            request.put("artist", artist);
            request.put("album", album);
            request.put("genre", genre);
            request.put("duration", duration);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    songServiceUrl + "/" + id,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "{\"error\": \"" + cleanErrorMessage(e.getResponseBodyAsString()) + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Connection error: " + e.getMessage() + "\"}";
        }
    }

    public String deleteSong(String id, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    songServiceUrl + "/" + id,
                    HttpMethod.DELETE,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            return "{\"error\": \"" + cleanErrorMessage(e.getResponseBodyAsString()) + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Connection error: " + e.getMessage() + "\"}";
        }
    }

    private String cleanErrorMessage(String errorResponse) {
        if (errorResponse == null) {
            return "Unknown error";
        }
        // Remove extra quotes and 400 status prefixes
        return errorResponse
                .replaceAll("400 : \"", "")
                .replaceAll("\"$", "")
                .replaceAll("^\"", "")
                .replaceAll("\\\\\"", "\"")
                .trim();
    }
}