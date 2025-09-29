package com.example.playlist.controller;

import com.example.playlist.dto.PlaylistRequest;
import com.example.playlist.model.Playlist;
import com.example.playlist.service.PlaylistService;
import com.example.playlist.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createPlaylist(
            @RequestBody PlaylistRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            Playlist playlist = playlistService.createPlaylist(request, token);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPlaylists() {
        try {
            List<Playlist> playlists = playlistService.getAllPlaylists();
            return ResponseEntity.ok(playlists);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-playlists")
    public ResponseEntity<?> getMyPlaylists(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            List<Playlist> playlists = playlistService.getMyPlaylists(token);
            return ResponseEntity.ok(playlists);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaylistById(@PathVariable String id) {
        try {
            return playlistService.getPlaylistById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/with-songs")
    public ResponseEntity<?> getPlaylistWithSongs(@PathVariable String id) {
        try {
            Map<String, Object> result = playlistService.getPlaylistWithSongs(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<?> addSongToPlaylist(
            @PathVariable String id,
            @PathVariable String songId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            Playlist playlist = playlistService.addSongToPlaylist(id, songId, token);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<?> removeSongFromPlaylist(
            @PathVariable String id,
            @PathVariable String songId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            Playlist playlist = playlistService.removeSongFromPlaylist(id, songId, token);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlaylist(
            @PathVariable String id,
            @RequestBody PlaylistRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            Playlist playlist = playlistService.updatePlaylist(id, request, token);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            playlistService.deletePlaylist(id, token);
            return ResponseEntity.ok("Playlist deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPlaylists(@RequestParam String name) {
        try {
            List<Playlist> playlists = playlistService.searchPlaylists(name);
            return ResponseEntity.ok(playlists);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Invalid Authorization header");
    }
}