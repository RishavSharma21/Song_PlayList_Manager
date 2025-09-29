package com.example.song.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongRequest {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private Integer duration; // in seconds
}