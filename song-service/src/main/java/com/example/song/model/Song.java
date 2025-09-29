package com.example.song.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "songs")
public class Song {

    @Id
    private String id;

    private String title;

    private String artist;

    private String album;

    private String genre;

    private Integer duration; // in seconds

    private String addedBy; // username who added the song

    private LocalDateTime createdAt;

    public Song(String title, String artist, String album, String genre, Integer duration, String addedBy) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
        this.addedBy = addedBy;
        this.createdAt = LocalDateTime.now();
    }
}