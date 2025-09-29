package com.example.song.service;

import com.example.song.dto.SongRequest;
import com.example.song.model.Song;
import com.example.song.repository.SongRepository;
import com.example.song.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final JwtUtil jwtUtil;

    public Song addSong(SongRequest request, String token) {
        String username = jwtUtil.extractUsername(token);

        Song song = new Song(
                request.getTitle(),
                request.getArtist(),
                request.getAlbum(),
                request.getGenre(),
                request.getDuration(),
                username
        );

        return songRepository.save(song);
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Optional<Song> getSongById(String id) {
        return songRepository.findById(id);
    }

    public List<Song> searchByTitle(String title) {
        return songRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Song> searchByArtist(String artist) {
        return songRepository.findByArtistContainingIgnoreCase(artist);
    }

    public List<Song> searchByGenre(String genre) {
        return songRepository.findByGenreIgnoreCase(genre);
    }

    public Song updateSong(String id, SongRequest request, String token) {
        String username = jwtUtil.extractUsername(token);

        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found!"));

        // Check if user is the owner
        if (!song.getAddedBy().equals(username)) {
            throw new RuntimeException("You can only update songs you added!");
        }

        song.setTitle(request.getTitle());
        song.setArtist(request.getArtist());
        song.setAlbum(request.getAlbum());
        song.setGenre(request.getGenre());
        song.setDuration(request.getDuration());

        return songRepository.save(song);
    }

    public void deleteSong(String id, String token) {
        String username = jwtUtil.extractUsername(token);

        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found!"));

        // Check if user is the owner
        if (!song.getAddedBy().equals(username)) {
            throw new RuntimeException("You can only delete songs you added!");
        }

        songRepository.deleteById(id);
    }

    public List<Song> getMySongs(String token) {
        String username = jwtUtil.extractUsername(token);
        return songRepository.findByAddedBy(username);
    }
}