package com.example.playlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PlaylistServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlaylistServiceApplication.class, args);
        System.out.println("✅ Playlist Service is running on port 8083");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}