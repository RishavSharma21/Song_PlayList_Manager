package com.example.playlist.service;

import com.example.playlist.client.SongServiceClient;
import com.example.playlist.dto.PlaylistRequest;
import com.example.playlist.dto.SongDTO;
import com.example.playlist.model.Playlist;
import com.example.playlist.repository.PlaylistRepository;
import com.example.playlist.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final JwtUtil jwtUtil;
    private final SongServiceClient songServiceClient;

    public Playlist createPlaylist(PlaylistRequest request, String token) {
        String username = jwtUtil.extractUsername(token);

        Playlist playlist = new Playlist(
                request.getName(),
                request.getDescription(),
                username
        );

        return playlistRepository.save(playlist);
    }

    public List<Playlist> getMyPlaylists(String token) {
        String username = jwtUtil.extractUsername(token);
        return playlistRepository.findByCreatedBy(username);
    }

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    public Optional<Playlist> getPlaylistById(String id) {
        return playlistRepository.findById(id);
    }

    public Map<String, Object> getPlaylistWithSongs(String id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found!"));

        // Fetch song details for each songId
        List<SongDTO> songs = playlist.getSongIds().stream()
                .map(songServiceClient::getSongById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("playlist", playlist);
        result.put("songs", songs);

        return result;
    }

    public Playlist addSongToPlaylist(String playlistId, String songId, String token) {
        String username = jwtUtil.extractUsername(token);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found!"));

        // Check ownership
        if (!playlist.getCreatedBy().equals(username)) {
            throw new RuntimeException("You can only modify your own playlists!");
        }

        // Check if song exists by calling song service
        SongDTO song = songServiceClient.getSongById(songId);
        if (song == null) {
            throw new RuntimeException("Song not found!");
        }

        // Check if song is already in playlist
        if (playlist.getSongIds().contains(songId)) {
            throw new RuntimeException("Song is already in the playlist!");
        }

        playlist.getSongIds().add(songId);
        playlist.setUpdatedAt(LocalDateTime.now());

        return playlistRepository.save(playlist);
    }

    public Playlist removeSongFromPlaylist(String playlistId, String songId, String token) {
        String username = jwtUtil.extractUsername(token);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found!"));

        // Check ownership
        if (!playlist.getCreatedBy().equals(username)) {
            throw new RuntimeException("You can only modify your own playlists!");
        }

        if (!playlist.getSongIds().contains(songId)) {
            throw new RuntimeException("Song is not in the playlist!");
        }

        playlist.getSongIds().remove(songId);
        playlist.setUpdatedAt(LocalDateTime.now());

        return playlistRepository.save(playlist);
    }

    public Playlist updatePlaylist(String id, PlaylistRequest request, String token) {
        String username = jwtUtil.extractUsername(token);

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found!"));

        // Check ownership
        if (!playlist.getCreatedBy().equals(username)) {
            throw new RuntimeException("You can only update your own playlists!");
        }

        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        playlist.setUpdatedAt(LocalDateTime.now());

        return playlistRepository.save(playlist);
    }

    public void deletePlaylist(String id, String token) {
        String username = jwtUtil.extractUsername(token);

        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found!"));

        // Check ownership
        if (!playlist.getCreatedBy().equals(username)) {
            throw new RuntimeException("You can only delete your own playlists!");
        }

        playlistRepository.deleteById(id);
    }

    public List<Playlist> searchPlaylists(String name) {
        return playlistRepository.findByNameContainingIgnoreCase(name);
    }
}