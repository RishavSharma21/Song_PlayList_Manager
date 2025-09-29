package com.example.playlist.repository;

import com.example.playlist.model.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, String> {
    List<Playlist> findByCreatedBy(String username);
    List<Playlist> findByNameContainingIgnoreCase(String name);
}