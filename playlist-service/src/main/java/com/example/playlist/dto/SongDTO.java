package com.example.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {
    private String id;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private Integer duration;
    private String addedBy;
    private LocalDateTime createdAt;
}