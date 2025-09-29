package com.example.playlist.client;

import com.example.playlist.dto.SongDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class SongServiceClient {

    private final RestTemplate restTemplate;

    @Value("${song.service.url}")
    private String songServiceUrl;

    public SongDTO getSongById(String songId) {
        try {
            String url = songServiceUrl + "/" + songId;
            return restTemplate.getForObject(url, SongDTO.class);
        } catch (Exception e) {
            System.err.println("Error fetching song: " + e.getMessage());
            return null;
        }
    }
}