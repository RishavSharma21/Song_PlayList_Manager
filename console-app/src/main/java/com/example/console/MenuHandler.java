package com.example.console.ui;

import com.example.console.client.AuthClient;
import com.example.console.client.PlaylistClient;
import com.example.console.client.SongClient;
import com.example.console.util.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class MenuHandler {

    private final AuthClient authClient;
    private final SongClient songClient;
    private final PlaylistClient playlistClient;
    private final SessionManager sessionManager;
    private final Scanner scanner = new Scanner(System.in);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void start() {
        printWelcomeBanner();

        while (true) {
            try {
                if (!sessionManager.isLoggedIn()) {
                    showAuthMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("         SONG & PLAYLIST MANAGEMENT SYSTEM");
        System.out.println("=".repeat(70));
        System.out.println("           Microservices Architecture Demo");
        System.out.println("=".repeat(70) + "\n");
    }

    private void showAuthMenu() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║           AUTHENTICATION MENU                    ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  1. Register                                     ║");
        System.out.println("║  2. Login                                        ║");
        System.out.println("║  3. Exit                                         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.print("\nChoice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1 -> handleRegister();
                case 2 -> handleLogin();
                case 3 -> {
                    System.out.println("\nGoodbye!");
                    System.exit(0);
                }
                default -> System.out.println("\nInvalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a valid number!");
        }
    }

    private void showMainMenu() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║              MAIN MENU                           ║");
        System.out.println("║  User: " + String.format("%-40s", sessionManager.getUsername()) +    "  ║  ");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  1. Song Management                              ║");
        System.out.println("║  2. Playlist Management                          ║");
        System.out.println("║  3. Logout                                       ║");
        System.out.println("║  4. Exit                                         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.print("\nChoice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1 -> showSongMenu();
                case 2 -> showPlaylistMenu();
                case 3 -> handleLogout();
                case 4 -> {
                    System.out.println("\nGoodbye!");
                    System.exit(0);
                }
                default -> System.out.println("\nInvalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a valid number!");
        }
    }

    private void handleRegister() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("USER REGISTRATION");
        System.out.println("-".repeat(50));

        String name = getValidInput("Name: ", false);
        String username = getValidInput("Username: ", false);
        String password = getValidInput("Password: ", false);
        String email = getValidInput("Email: ", false);

        System.out.println("\nRegistering...");
        Map<String, String> result = authClient.register(name, username, password, email);

        if (result.containsKey("error")) {
            System.out.println("Registration failed: " + result.get("error"));
        } else {
            sessionManager.setSession(result.get("token"), result.get("username"));
            System.out.println("Registration successful! Welcome, " + result.get("username"));
        }
    }

    private void handleLogin() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("USER LOGIN");
        System.out.println("-".repeat(50));

        String username = getValidInput("Username: ", false);
        String password = getValidInput("Password: ", false);

        System.out.println("\nLogging in...");
        Map<String, String> result = authClient.login(username, password);

        if (result.containsKey("error")) {
            System.out.println("Login failed: " + result.get("error"));
        } else {
            sessionManager.setSession(result.get("token"), result.get("username"));
            System.out.println("Login successful! Welcome back, " + result.get("username"));
        }
    }

    private void handleLogout() {
        sessionManager.clearSession();
        System.out.println("\nLogged out successfully!");
    }

    private void showSongMenu() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║            SONG MANAGEMENT                       ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  1. Add Song                                     ║");
        System.out.println("║  2. View All Songs                               ║");
        System.out.println("║  3. View My Songs                                ║");
        System.out.println("║  4. Search Songs                                 ║");
        System.out.println("║  5. Update Song                                  ║");
        System.out.println("║  6. Delete Song                                  ║");
        System.out.println("║  7. Back                                         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.print("\nChoice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1 -> handleAddSong();
                case 2 -> handleViewAllSongs();
                case 3 -> handleViewMySongs();
                case 4 -> handleSearchSongs();
                case 5 -> handleUpdateSong();
                case 6 -> handleDeleteSong();
                case 7 -> {}
                default -> System.out.println("\nInvalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a valid number!");
        }
    }

    private void handleAddSong() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("ADD NEW SONG");
        System.out.println("-".repeat(50));

        String title = getValidInput("Title: ", false);
        String artist = getValidInput("Artist: ", false);
        String album = getValidInput("Album: ", false);
        String genre = getValidInput("Genre: ", false);
        int duration = getValidInteger("Duration (seconds): ", 1, 7200);

        System.out.println("\nAdding song...");
        String result = songClient.addSong(title, artist, album, genre, duration, sessionManager.getToken());

        parseAndDisplayResult(result, "Song added successfully!", "Song ID");
    }

    private void handleViewAllSongs() {
        System.out.println("\nFetching all songs...");
        String result = songClient.getAllSongs();

        try {
            JsonNode songs = objectMapper.readTree(result);

            if (songs.isArray() && songs.size() > 0) {
                printSongsTable(songs, "ALL SONGS");
            } else {
                System.out.println("\nNo songs found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleViewMySongs() {
        System.out.println("\nFetching your songs...");
        String result = songClient.getMySongs(sessionManager.getToken());

        try {
            JsonNode songs = objectMapper.readTree(result);

            if (songs.isArray() && songs.size() > 0) {
                printSongsTable(songs, "MY SONGS");
            } else {
                System.out.println("\nYou haven't added any songs yet.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printSongsTable(JsonNode songs, String title) {
        System.out.println("\n" + "=".repeat(110));
        System.out.println(centerText(title, 110));
        System.out.println("=".repeat(110));
        System.out.printf("%-3s %-25s %-20s %-20s %-15s %-10s%n",
                "No", "Title", "Artist", "Album", "Genre", "Duration");
        System.out.println("-".repeat(110));

        int index = 1;
        for (JsonNode song : songs) {
            String songTitle = song.has("title") ? song.get("title").asText() : "Unknown";
            String artist = song.has("artist") ? song.get("artist").asText() : "Unknown";
            String album = song.has("album") ? song.get("album").asText() : "Unknown";
            String genre = song.has("genre") ? song.get("genre").asText() : "Unknown";
            String duration = song.has("duration") ? formatDuration(song.get("duration").asInt()) : "0:00";

            System.out.printf("%-3d %-25s %-20s %-20s %-15s %-10s%n",
                    index++,
                    truncate(songTitle, 25),
                    truncate(artist, 20),
                    truncate(album, 20),
                    truncate(genre, 15),
                    duration);
        }
        System.out.println("=".repeat(110));
        System.out.println("Total: " + songs.size() + " songs");
    }

    private void handleSearchSongs() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("SEARCH SONGS");
        System.out.println("-".repeat(50));
        System.out.println("1. By Title");
        System.out.println("2. By Artist");
        System.out.println("3. By Genre");
        System.out.print("\nChoice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            String searchType = "";
            String prompt = "";

            switch (choice) {
                case 1 -> { searchType = "title"; prompt = "Enter title: "; }
                case 2 -> { searchType = "artist"; prompt = "Enter artist: "; }
                case 3 -> { searchType = "genre"; prompt = "Enter genre: "; }
                default -> {
                    System.out.println("Invalid choice!");
                    return;
                }
            }

            String searchValue = getValidInput(prompt, false);

            System.out.println("\nSearching...");
            String result = songClient.searchSongs(searchType, searchValue);

            JsonNode songs = objectMapper.readTree(result);
            if (songs.isArray() && songs.size() > 0) {
                printSongsTable(songs, "SEARCH RESULTS");
            } else {
                System.out.println("\nNo songs found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleUpdateSong() {
        handleViewMySongs();

        System.out.println("\n" + "-".repeat(50));
        System.out.println("UPDATE SONG");
        System.out.println("-".repeat(50));

        int songNumber = getValidInteger("Enter song number to update: ", 1, Integer.MAX_VALUE);

        String songId = getSongIdByNumber(songNumber);
        if (songId == null) {
            System.out.println("Invalid song number!");
            return;
        }

        String title = getValidInput("New Title: ", false);
        String artist = getValidInput("New Artist: ", false);
        String album = getValidInput("New Album: ", false);
        String genre = getValidInput("New Genre: ", false);
        int duration = getValidInteger("New Duration (seconds): ", 1, 7200);

        System.out.println("\nUpdating song...");
        String result = songClient.updateSong(songId, title, artist, album, genre, duration, sessionManager.getToken());

        parseAndDisplayResult(result, "Song updated successfully!", null);
    }

    private void handleDeleteSong() {
        handleViewMySongs();

        System.out.println("\n" + "-".repeat(50));
        System.out.println("DELETE SONG");
        System.out.println("-".repeat(50));

        int songNumber = getValidInteger("Enter song number to delete: ", 1, Integer.MAX_VALUE);

        String songId = getSongIdByNumber(songNumber);
        if (songId == null) {
            System.out.println("Invalid song number!");
            return;
        }

        String confirm = getValidInput("Are you sure? (yes/no): ", false);
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        System.out.println("\nDeleting song...");
        String result = songClient.deleteSong(songId, sessionManager.getToken());

        if (result.contains("success") || result.contains("deleted")) {
            System.out.println("Song deleted successfully!");
        } else {
            System.out.println("Failed: " + result);
        }
    }

    private void showPlaylistMenu() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║          PLAYLIST MANAGEMENT                     ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  1. Create Playlist                              ║");
        System.out.println("║  2. View All Playlists                           ║");
        System.out.println("║  3. View My Playlists                            ║");
        System.out.println("║  4. View Playlist Details                        ║");
        System.out.println("║  5. Add Song to Playlist                         ║");
        System.out.println("║  6. Remove Song from Playlist                    ║");
        System.out.println("║  7. Delete Playlist                              ║");
        System.out.println("║  8. Back                                         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.print("\nChoice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1 -> handleCreatePlaylist();
                case 2 -> handleViewAllPlaylists();
                case 3 -> handleViewMyPlaylists();
                case 4 -> handleViewPlaylistDetails();
                case 5 -> handleAddSongToPlaylist();
                case 6 -> handleRemoveSongFromPlaylist();
                case 7 -> handleDeletePlaylist();
                case 8 -> {}
                default -> System.out.println("\nInvalid choice!");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a valid number!");
        }
    }

    private void handleCreatePlaylist() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("CREATE PLAYLIST");
        System.out.println("-".repeat(50));

        String name = getValidInput("Playlist Name: ", false);
        String description = getValidInput("Description: ", true);

        System.out.println("\nCreating playlist...");
        String result = playlistClient.createPlaylist(name, description, sessionManager.getToken());

        parseAndDisplayResult(result, "Playlist created successfully!", "Playlist ID");
    }

    private void handleViewAllPlaylists() {
        System.out.println("\nFetching all playlists...");
        String result = playlistClient.getAllPlaylists();

        try {
            JsonNode playlists = objectMapper.readTree(result);

            if (playlists.isArray() && playlists.size() > 0) {
                printPlaylistsTable(playlists, "ALL PLAYLISTS");
            } else {
                System.out.println("\nNo playlists found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleViewMyPlaylists() {
        System.out.println("\nFetching your playlists...");
        String result = playlistClient.getMyPlaylists(sessionManager.getToken());

        try {
            JsonNode playlists = objectMapper.readTree(result);

            if (playlists.isArray() && playlists.size() > 0) {
                printPlaylistsTable(playlists, "MY PLAYLISTS");
            } else {
                System.out.println("\nYou haven't created any playlists yet.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printPlaylistsTable(JsonNode playlists, String title) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println(centerText(title, 100));
        System.out.println("=".repeat(100));
        System.out.printf("%-3s %-30s %-40s %-10s%n", "No", "Name", "Description", "Songs");
        System.out.println("-".repeat(100));

        int index = 1;
        for (JsonNode playlist : playlists) {
            String name = playlist.has("name") ? playlist.get("name").asText() : "Unknown";
            String desc = playlist.has("description") ? playlist.get("description").asText() : "No description";
            int songCount = playlist.has("songIds") ? playlist.get("songIds").size() : 0;

            System.out.printf("%-3d %-30s %-40s %-10d%n",
                    index++,
                    truncate(name, 30),
                    truncate(desc, 40),
                    songCount);
        }
        System.out.println("=".repeat(100));
        System.out.println("Total: " + playlists.size() + " playlists");
    }

    private void handleViewPlaylistDetails() {
        handleViewMyPlaylists();

        System.out.println("\n" + "-".repeat(50));
        System.out.println("PLAYLIST DETAILS");
        System.out.println("-".repeat(50));

        int playlistNumber = getValidInteger("Enter playlist number: ", 1, Integer.MAX_VALUE);

        String playlistId = getPlaylistIdByNumber(playlistNumber);
        if (playlistId == null) {
            System.out.println("Invalid playlist number!");
            return;
        }

        System.out.println("\nFetching details...");
        String result = playlistClient.getPlaylistWithSongs(playlistId);

        try {
            JsonNode data = objectMapper.readTree(result);

            if (data.has("playlist")) {
                JsonNode playlist = data.get("playlist");
                System.out.println("\n" + "=".repeat(80));
                System.out.println("Playlist: " + playlist.get("name").asText());
                System.out.println("Description: " + playlist.get("description").asText());
                System.out.println("=".repeat(80));

                if (data.has("songs") && data.get("songs").isArray()) {
                    JsonNode songs = data.get("songs");
                    if (songs.size() > 0) {
                        printSongsTable(songs, "SONGS IN PLAYLIST");
                    } else {
                        System.out.println("\nThis playlist is empty.");
                    }
                }
            } else if (data.has("error")) {
                System.out.println("Error: " + data.get("error").asText());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleAddSongToPlaylist() {
        handleViewAllSongs();
        System.out.println();
        handleViewMyPlaylists();

        System.out.println("\n" + "-".repeat(50));
        System.out.println("ADD SONG TO PLAYLIST");
        System.out.println("-".repeat(50));

        int playlistNumber = getValidInteger("Enter playlist number: ", 1, Integer.MAX_VALUE);
        int songNumber = getValidInteger("Enter song number: ", 1, Integer.MAX_VALUE);

        String playlistId = getPlaylistIdByNumber(playlistNumber);
        String songId = getAllSongIdByNumber(songNumber);

        if (playlistId == null || songId == null) {
            System.out.println("Invalid selection!");
            return;
        }

        System.out.println("\nAdding song to playlist...");
        String result = playlistClient.addSongToPlaylist(playlistId, songId, sessionManager.getToken());

        parseAndDisplayResult(result, "Song added to playlist!", null);
    }

    private void handleRemoveSongFromPlaylist() {
        handleViewMyPlaylists();

        System.out.println("\n" + "-".repeat(50));
        System.out.println("REMOVE SONG FROM PLAYLIST");
        System.out.println("-".repeat(50));

        int playlistNumber = getValidInteger("Enter playlist number: ", 1, Integer.MAX_VALUE);

        String playlistId = getPlaylistIdByNumber(playlistNumber);
        if (playlistId == null) {
            System.out.println("Invalid playlist number!");
            return;
        }

        // Show playlist songs
        handleViewPlaylistDetails();

        int songNumber = getValidInteger("Enter song number to remove: ", 1, Integer.MAX_VALUE);
        String songId = getSongIdByNumber(songNumber);

        if (songId == null) {
            System.out.println("Invalid song number!");
            return;
        }

        String confirm = getValidInput("Are you sure? (yes/no): ", false);
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Operation cancelled.");
            return;
        }

        System.out.println("\nRemoving song...");
        String result = playlistClient.removeSongFromPlaylist(playlistId, songId, sessionManager.getToken());

        parseAndDisplayResult(result, "Song removed from playlist!", null);
    }

    private void handleDeletePlaylist() {
        handleViewMyPlaylists();

        System.out.println("\n" + "-".repeat(50));
        System.out.println("DELETE PLAYLIST");
        System.out.println("-".repeat(50));

        int playlistNumber = getValidInteger("Enter playlist number: ", 1, Integer.MAX_VALUE);

        String playlistId = getPlaylistIdByNumber(playlistNumber);
        if (playlistId == null) {
            System.out.println("Invalid playlist number!");
            return;
        }

        String confirm = getValidInput("Are you sure? (yes/no): ", false);
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        System.out.println("\nDeleting playlist...");
        String result = playlistClient.deletePlaylist(playlistId, sessionManager.getToken());

        if (result.contains("success") || result.contains("deleted")) {
            System.out.println("Playlist deleted successfully!");
        } else {
            System.out.println("Failed: " + result);
        }
    }

    // Helper methods
    private String getValidInput(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty() || allowEmpty) {
                return input.isEmpty() ? "N/A" : input;
            }
            System.out.println("This field cannot be empty!");
        }
    }

    private int getValidInteger(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Value must be between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private String getSongIdByNumber(int number) {
        try {
            String result = songClient.getMySongs(sessionManager.getToken());
            JsonNode songs = objectMapper.readTree(result);

            if (songs.isArray() && number > 0 && number <= songs.size()) {
                return songs.get(number - 1).get("id").asText();
            }
        } catch (Exception e) {
            System.out.println("Error fetching song: " + e.getMessage());
        }
        return null;
    }

    private String getAllSongIdByNumber(int number) {
        try {
            String result = songClient.getAllSongs();
            JsonNode songs = objectMapper.readTree(result);

            if (songs.isArray() && number > 0 && number <= songs.size()) {
                return songs.get(number - 1).get("id").asText();
            }
        } catch (Exception e) {
            System.out.println("Error fetching song: " + e.getMessage());
        }
        return null;
    }

    private String getPlaylistIdByNumber(int number) {
        try {
            String result = playlistClient.getMyPlaylists(sessionManager.getToken());
            JsonNode playlists = objectMapper.readTree(result);

            if (playlists.isArray() && number > 0 && number <= playlists.size()) {
                return playlists.get(number - 1).get("id").asText();
            }
        } catch (Exception e) {
            System.out.println("Error fetching playlist: " + e.getMessage());
        }
        return null;
    }

    private void parseAndDisplayResult(String result, String successMessage, String idLabel) {
        try {
            JsonNode jsonNode = objectMapper.readTree(result);
            if (jsonNode.has("id")) {
                System.out.println(successMessage);
                if (idLabel != null) {
                    System.out.println(idLabel + ": " + jsonNode.get("id").asText());
                }
            } else if (jsonNode.has("error")) {
                String error = jsonNode.get("error").asText();
                // Clean up error message
                error = error.replaceAll("400 : \"", "").replaceAll("\"", "");
                System.out.println("Error: " + error);
            } else {
                System.out.println(successMessage);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }
}


//
//package com.example.console.ui;
//
//import com.example.console.client.AuthClient;
//import com.example.console.client.PlaylistClient;
//import com.example.console.client.SongClient;
//import com.example.console.util.SessionManager;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.function.Supplier;
//
//@Component
//@RequiredArgsConstructor
//public class MenuHandler {
//
//    private final AuthClient authClient;
//    private final SongClient songClient;
//    private final PlaylistClient playlistClient;
//    private final SessionManager sessionManager;
//    private final Scanner scanner = new Scanner(System.in);
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public void start() {
//        printWelcomeBanner();
//
//        while (true) {
//            try {
//                if (!sessionManager.isLoggedIn()) {
//                    showAuthMenu();
//                } else {
//                    showMainMenu();
//                }
//            } catch (Exception e) {
//                System.out.println("\n❌ Unexpected error: " + e.getMessage());
//                System.out.println("Press Enter to continue...");
//                scanner.nextLine();
//            }
//        }
//    }
//
//    private void printWelcomeBanner() {
//        System.out.println("\n" + "✨" + "═".repeat(70) + "✨");
//        System.out.println("🎵".repeat(3) + "       SONG & PLAYLIST MANAGEMENT SYSTEM       " + "🎵".repeat(3));
//        System.out.println("✨" + "═".repeat(70) + "✨");
//        System.out.println("           Microservices Architecture Console");
//        System.out.println("✨" + "═".repeat(70) + "✨\n");
//    }
//
//    private void showAuthMenu() {
//        System.out.println("\n╔══════════════════════════════════════════════════╗");
//        System.out.println("║              AUTHENTICATION MENU                 ║");
//        System.out.println("╠══════════════════════════════════════════════════╣");
//        System.out.println("║  1. 👤 Register                                  ║");
//        System.out.println("║  2. 🔑 Login                                     ║");
//        System.out.println("║  3. ℹ️  Help                                     ║");
//        System.out.println("║  4. 🚪 Exit                                      ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//        System.out.print("\n🎯 Choice: ");
//
//        try {
//            int choice = getValidatedIntegerInput("", 1, 4);
//
//            switch (choice) {
//                case 1 -> handleRegister();
//                case 2 -> handleLogin();
//                case 3 -> showHelp();
//                case 4 -> {
//                    System.out.println("\n👋 Thank you for using our system! Goodbye!");
//                    System.exit(0);
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("\n❌ Menu error: " + e.getMessage());
//        }
//    }
//
//    private void showMainMenu() {
//        System.out.println("\n╔══════════════════════════════════════════════════╗");
//        System.out.println("║                  MAIN MENU                       ║");
//        System.out.println("║  Welcome: " + String.format("%-38s", sessionManager.getUsername()) +     "  ║  ");
//        System.out.println("╠══════════════════════════════════════════════════╣");
//        System.out.println("║  1. 🎵  Song Management                          ║");
//        System.out.println("║  2. 📋  Playlist Management                      ║");
//        System.out.println("║  3. 💾  Export My Data                           ║");
//        System.out.println("║  4. ℹ️   Help & Tips                             ║");
//        System.out.println("║  5. 🚪  Logout                                   ║");
//        System.out.println("║  6. ❌  Exit                                     ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//        System.out.print("\n🎯 Choice: ");
//
//        try {
//            int choice = getValidatedIntegerInput("", 1, 6);
//
//            switch (choice) {
//                case 1 -> showSongMenu();
//                case 2 -> showPlaylistMenu();
//                case 3 -> exportMyData();
//                case 4 -> showHelp();
//                case 5 -> handleLogout();
//                case 6 -> {
//                    System.out.println("\n👋 Thank you for using our system! Goodbye!");
//                    System.exit(0);
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("\n❌ Menu error: " + e.getMessage());
//        }
//    }
//
//    private void handleRegister() {
//        printSectionHeader("👤 USER REGISTRATION");
//
//        try {
//            System.out.print("🎯 Full Name: ");
//            String name = scanner.nextLine().trim();
//
//            String username = getValidatedInput("📝 Username: ",
//                    "Username must be 3-20 characters",
//                    input -> input.length() >= 3 && input.length() <= 20);
//
//            String password = getValidatedInput("🔒 Password: ",
//                    "Password must be at least 6 characters",
//                    input -> input.length() >= 6);
//
//            String email = getValidatedInput("📧 Email: ",
//                    "Please enter a valid email address",
//                    this::isValidEmail);
//
//            showLoading("Creating your account");
//            Map<String, String> result = authClient.register(name, username, password, email);
//
//            if (result.containsKey("error")) {
//                printError("Registration failed: " + cleanErrorMessage(result.get("error")));
//            } else {
//                printSuccess("🎉 Registration successful!");
//                sessionManager.setSession(result.get("token"), result.get("username"));
//                printSuccess("Welcome to the system, " + result.get("username") + "! 🌟");
//            }
//        } catch (Exception e) {
//            printError("Registration cancelled: " + e.getMessage());
//        }
//    }
//
//    private void handleLogin() {
//        printSectionHeader("🔑 USER LOGIN");
//
//        try {
//            String username = getValidatedInput("📝 Username: ",
//                    "Username cannot be empty", input -> !input.trim().isEmpty());
//
//            System.out.print("🔒 Password: ");
//            String password = scanner.nextLine();
//
//            if (password.isEmpty()) {
//                printError("Password cannot be empty!");
//                return;
//            }
//
//            showLoading("Authenticating");
//            Map<String, String> result = authClient.login(username, password);
//
//            if (result.containsKey("error")) {
//                printError("Login failed: " + cleanErrorMessage(result.get("error")));
//            } else {
//                printSuccess("✅ Login successful!");
//                sessionManager.setSession(result.get("token"), result.get("username"));
//                printSuccess("Welcome back, " + result.get("username") + "! 🎉");
//            }
//        } catch (Exception e) {
//            printError("Login process failed: " + e.getMessage());
//        }
//    }
//
//    private void handleLogout() {
//        try {
//            sessionManager.clearSession();
//            printSuccess("✅ Logged out successfully! See you soon! 👋");
//        } catch (Exception e) {
//            printError("Logout failed: " + e.getMessage());
//        }
//    }
//
//    private void showSongMenu() {
//        System.out.println("\n╔══════════════════════════════════════════════════╗");
//        System.out.println("║               SONG MANAGEMENT                    ║");
//        System.out.println("╠══════════════════════════════════════════════════╣");
//        System.out.println("║  1. ➕ Add New Song                              ║");
//        System.out.println("║  2. 📋 View All Songs                            ║");
//        System.out.println("║  3. 👤 View My Songs                             ║");
//        System.out.println("║  4. 🔍 Search Songs                              ║");
//        System.out.println("║  5. ✏️  Update Song                              ║");
//        System.out.println("║  6. 🗑️  Delete Song                              ║");
//        System.out.println("║  7. ↩️  Back to Main Menu                        ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//        System.out.print("\n🎯 Choice: ");
//
//        try {
//            int choice = getValidatedIntegerInput("", 1, 7);
//
//            switch (choice) {
//                case 1 -> handleAddSong();
//                case 2 -> handleViewAllSongs();
//                case 3 -> handleViewMySongs();
//                case 4 -> handleSearchSongs();
//                case 5 -> handleUpdateSong();
//                case 6 -> handleDeleteSong();
//                case 7 -> {}
//            }
//        } catch (Exception e) {
//            printError("Song menu error: " + e.getMessage());
//        }
//    }
//
//    private void handleAddSong() {
//        if (!validateSession()) return;
//        printSectionHeader("➕ ADD NEW SONG");
//
//        try {
//            String title = getValidatedInput("🎵 Title: ",
//                    "Title cannot be empty", input -> !input.trim().isEmpty());
//
//            String artist = getValidatedInput("🎤 Artist: ",
//                    "Artist cannot be empty", input -> !input.trim().isEmpty());
//
//            String album = getValidatedInput("💿 Album: ",
//                    "Album cannot be empty", input -> !input.trim().isEmpty());
//
//            String genre = getValidatedInput("🎭 Genre: ",
//                    "Genre cannot be empty", input -> !input.trim().isEmpty());
//
//            int duration = getValidatedIntegerInput("⏱️ Duration (seconds): ", 1, 7200);
//
//            // Preview
//            System.out.println("\n" + "─".repeat(50));
//            System.out.println("📋 SONG PREVIEW:");
//            System.out.println("   Title: " + title);
//            System.out.println("   Artist: " + artist);
//            System.out.println("   Album: " + album);
//            System.out.println("   Genre: " + genre);
//            System.out.println("   Duration: " + formatDuration(duration));
//            System.out.println("─".repeat(50));
//
//            if (confirmAction("add this song")) {
//                showLoading("Adding song to library");
//                String result = songClient.addSong(title, artist, album, genre, duration, sessionManager.getToken());
//                parseAndDisplayResult(result, "Song added successfully!", "Song ID");
//            } else {
//                printInfo("Song addition cancelled.");
//            }
//        } catch (Exception e) {
//            printError("Failed to add song: " + e.getMessage());
//        }
//    }
//
//    private void handleViewAllSongs() {
//        showLoading("Loading all songs");
//        String result = withRetry(() -> songClient.getAllSongs(), "fetch songs", 2);
//
//        try {
//            JsonNode songs = objectMapper.readTree(result);
//            if (songs.isArray() && songs.size() > 0) {
//                printEnhancedSongsTable(songs, "🎵 ALL SONGS");
//            } else {
//                printInfo("📭 No songs found in the library.");
//            }
//        } catch (Exception e) {
//            printError("Failed to load songs: " + e.getMessage());
//        }
//    }
//
//    private void handleViewMySongs() {
//        if (!validateSession()) return;
//        showLoading("Loading your songs");
//        String result = withRetry(() -> songClient.getMySongs(sessionManager.getToken()), "fetch your songs", 2);
//
//        try {
//            JsonNode songs = objectMapper.readTree(result);
//            if (songs.isArray() && songs.size() > 0) {
//                printEnhancedSongsTable(songs, "👤 MY SONGS");
//            } else {
//                printInfo("📭 You haven't added any songs yet.");
//                System.out.println("💡 Use 'Add New Song' to start your collection!");
//            }
//        } catch (Exception e) {
//            printError("Failed to load your songs: " + e.getMessage());
//        }
//    }
//
//    private void printEnhancedSongsTable(JsonNode songs, String title) {
//        try {
//            int[] widths = calculateSongColumnWidths(songs);
//            int totalWidth = widths[0] + widths[1] + widths[2] + widths[3] + widths[4] + widths[5] + 13;
//
//            System.out.println("\n┌" + "─".repeat(totalWidth - 2) + "┐");
//            System.out.println("│" + centerText(title, totalWidth - 2) + "│");
//            System.out.println("├" + "─".repeat(widths[0] + 2) + "┼" + "─".repeat(widths[1] + 2) + "┼" +
//                    "─".repeat(widths[2] + 2) + "┼" + "─".repeat(widths[3] + 2) + "┼" +
//                    "─".repeat(widths[4] + 2) + "┼" + "─".repeat(widths[5] + 2) + "┤");
//
//            // Header
//            System.out.printf("│ %-" + widths[0] + "s │ %-" + widths[1] + "s │ %-" + widths[2] + "s │ %-" +
//                            widths[3] + "s │ %-" + widths[4] + "s │ %-" + widths[5] + "s │%n",
//                    "#", "TITLE", "ARTIST", "ALBUM", "GENRE", "DURATION");
//
//            System.out.println("├" + "─".repeat(widths[0] + 2) + "┼" + "─".repeat(widths[1] + 2) + "┼" +
//                    "─".repeat(widths[2] + 2) + "┼" + "─".repeat(widths[3] + 2) + "┼" +
//                    "─".repeat(widths[4] + 2) + "┼" + "─".repeat(widths[5] + 2) + "┤");
//
//            // Rows
//            int index = 1;
//            for (JsonNode song : songs) {
//                System.out.printf("│ %-" + widths[0] + "d │ %-" + widths[1] + "s │ %-" + widths[2] + "s │ %-" +
//                                widths[3] + "s │ %-" + widths[4] + "s │ %-" + widths[5] + "s │%n",
//                        index++,
//                        truncate(song.get("title").asText(), widths[1]),
//                        truncate(song.get("artist").asText(), widths[2]),
//                        truncate(song.get("album").asText(), widths[3]),
//                        truncate(song.get("genre").asText(), widths[4]),
//                        formatDuration(song.get("duration").asInt()));
//            }
//
//            System.out.println("└" + "─".repeat(widths[0] + 2) + "┴" + "─".repeat(widths[1] + 2) + "┴" +
//                    "─".repeat(widths[2] + 2) + "┴" + "─".repeat(widths[3] + 2) + "┴" +
//                    "─".repeat(widths[4] + 2) + "┴" + "─".repeat(widths[5] + 2) + "┘");
//            System.out.println("📊 Total: " + songs.size() + " song(s)");
//        } catch (Exception e) {
//            printError("Failed to display songs: " + e.getMessage());
//        }
//    }
//
//    private void handleSearchSongs() {
//        printSectionHeader("🔍 SEARCH SONGS");
//
//        try {
//            System.out.println("1. 🔎 By Title");
//            System.out.println("2. 🎤 By Artist");
//            System.out.println("3. 💿 By Album");
//            System.out.println("4. 🎭 By Genre");
//            System.out.println("5. ↩️ Back");
//
//            int choice = getValidatedIntegerInput("\n🎯 Choice: ", 1, 5);
//            if (choice == 5) return;
//
//            String[] types = {"title", "artist", "album", "genre"};
//            String[] prompts = {"Enter title to search: ", "Enter artist to search: ",
//                    "Enter album to search: ", "Enter genre to search: "};
//
//            String searchValue = getValidatedInput(prompts[choice - 1],
//                    "Search term cannot be empty", input -> !input.trim().isEmpty());
//
//            showLoading("Searching songs");
//            String result = songClient.searchSongs(types[choice - 1], searchValue);
//
//            JsonNode songs = objectMapper.readTree(result);
//            if (songs.isArray() && songs.size() > 0) {
//                printEnhancedSongsTable(songs, "🔍 SEARCH RESULTS");
//                printSuccess("Found " + songs.size() + " matching song(s)");
//            } else {
//                printInfo("📭 No songs found matching '" + searchValue + "'");
//                System.out.println("💡 Try different keywords or check spelling");
//            }
//        } catch (Exception e) {
//            printError("Search failed: " + e.getMessage());
//        }
//    }
//
//    private void handleUpdateSong() {
//        if (!validateSession()) return;
//        handleViewMySongs();
//
//        printSectionHeader("✏️ UPDATE SONG");
//
//        try {
//            int songNumber = getValidatedIntegerInput("Enter song number to update: ", 1, Integer.MAX_VALUE);
//            String songId = getSongIdByNumber(songNumber);
//
//            if (songId == null) {
//                printError("Invalid song number!");
//                return;
//            }
//
//            System.out.print("🎵 New Title: ");
//            String title = scanner.nextLine().trim();
//            System.out.print("🎤 New Artist: ");
//            String artist = scanner.nextLine().trim();
//            System.out.print("💿 New Album: ");
//            String album = scanner.nextLine().trim();
//            System.out.print("🎭 New Genre: ");
//            String genre = scanner.nextLine().trim();
//            int duration = getValidatedIntegerInput("⏱️ New Duration (seconds): ", 1, 7200);
//
//            if (confirmAction("update this song")) {
//                showLoading("Updating song");
//                String result = songClient.updateSong(songId, title, artist, album, genre, duration, sessionManager.getToken());
//                parseAndDisplayResult(result, "Song updated successfully!", null);
//            } else {
//                printInfo("Update cancelled.");
//            }
//        } catch (Exception e) {
//            printError("Update failed: " + e.getMessage());
//        }
//    }
//
//    private void handleDeleteSong() {
//        if (!validateSession()) return;
//        handleViewMySongs();
//
//        printSectionHeader("🗑️ DELETE SONG");
//
//        try {
//            int songNumber = getValidatedIntegerInput("Enter song number to delete: ", 1, Integer.MAX_VALUE);
//            String songId = getSongIdByNumber(songNumber);
//
//            if (songId == null) {
//                printError("Invalid song number!");
//                return;
//            }
//
//            if (confirmAction("permanently delete this song")) {
//                showLoading("Deleting song");
//                String result = songClient.deleteSong(songId, sessionManager.getToken());
//
//                if (result.contains("success") || result.contains("deleted")) {
//                    printSuccess("✅ Song deleted successfully!");
//                } else {
//                    printError("Failed to delete song: " + cleanErrorMessage(result));
//                }
//            } else {
//                printInfo("Deletion cancelled.");
//            }
//        } catch (Exception e) {
//            printError("Deletion failed: " + e.getMessage());
//        }
//    }
//
//    private void showPlaylistMenu() {
//        System.out.println("\n╔══════════════════════════════════════════════════╗");
//        System.out.println("║             PLAYLIST MANAGEMENT                  ║");
//        System.out.println("╠══════════════════════════════════════════════════╣");
//        System.out.println("║  1. ➕ Create Playlist                           ║");
//        System.out.println("║  2. 📋 View All Playlists                        ║");
//        System.out.println("║  3. 👤 View My Playlists                         ║");
//        System.out.println("║  4. 🔍 View Playlist Details                     ║");
//        System.out.println("║  5. ➕ Add Song to Playlist                      ║");
//        System.out.println("║  6. ➖ Remove Song from Playlist                 ║");
//        System.out.println("║  7. 🗑️  Delete Playlist                          ║");
//        System.out.println("║  8. ↩️  Back to Main Menu                        ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//        System.out.print("\n🎯 Choice: ");
//
//        try {
//            int choice = getValidatedIntegerInput("", 1, 8);
//
//            switch (choice) {
//                case 1 -> handleCreatePlaylist();
//                case 2 -> handleViewAllPlaylists();
//                case 3 -> handleViewMyPlaylists();
//                case 4 -> handleViewPlaylistDetails();
//                case 5 -> handleAddSongToPlaylist();
//                case 6 -> handleRemoveSongFromPlaylist();
//                case 7 -> handleDeletePlaylist();
//                case 8 -> {}
//            }
//        } catch (Exception e) {
//            printError("Playlist menu error: " + e.getMessage());
//        }
//    }
//
//    private void handleCreatePlaylist() {
//        if (!validateSession()) return;
//        printSectionHeader("➕ CREATE PLAYLIST");
//
//        try {
//            String name = getValidatedInput("📝 Playlist Name: ",
//                    "Name cannot be empty", input -> !input.trim().isEmpty());
//
//            System.out.print("💬 Description: ");
//            String description = scanner.nextLine().trim();
//            if (description.isEmpty()) description = "No description";
//
//            if (confirmAction("create this playlist")) {
//                showLoading("Creating playlist");
//                String result = playlistClient.createPlaylist(name, description, sessionManager.getToken());
//                parseAndDisplayResult(result, "Playlist created successfully!", "Playlist ID");
//            } else {
//                printInfo("Playlist creation cancelled.");
//            }
//        } catch (Exception e) {
//            printError("Failed to create playlist: " + e.getMessage());
//        }
//    }
//
//    private void handleViewAllPlaylists() {
//        showLoading("Loading all playlists");
//        String result = withRetry(() -> playlistClient.getAllPlaylists(), "fetch playlists", 2);
//
//        try {
//            JsonNode playlists = objectMapper.readTree(result);
//            if (playlists.isArray() && playlists.size() > 0) {
//                printEnhancedPlaylistsTable(playlists, "📋 ALL PLAYLISTS");
//            } else {
//                printInfo("📭 No playlists found.");
//            }
//        } catch (Exception e) {
//            printError("Failed to load playlists: " + e.getMessage());
//        }
//    }
//
//    private void handleViewMyPlaylists() {
//        if (!validateSession()) return;
//        showLoading("Loading your playlists");
//        String result = withRetry(() -> playlistClient.getMyPlaylists(sessionManager.getToken()), "fetch your playlists", 2);
//
//        try {
//            JsonNode playlists = objectMapper.readTree(result);
//            if (playlists.isArray() && playlists.size() > 0) {
//                printEnhancedPlaylistsTable(playlists, "👤 MY PLAYLISTS");
//            } else {
//                printInfo("📭 You haven't created any playlists yet.");
//                System.out.println("💡 Use 'Create Playlist' to organize your music!");
//            }
//        } catch (Exception e) {
//            printError("Failed to load your playlists: " + e.getMessage());
//        }
//    }
//
//    private void printEnhancedPlaylistsTable(JsonNode playlists, String title) {
//        try {
//            int[] widths = {4, 25, 35, 8}; // #, NAME, DESCRIPTION, SONGS
//            int totalWidth = widths[0] + widths[1] + widths[2] + widths[3] + 9;
//
//            System.out.println("\n┌" + "─".repeat(totalWidth - 2) + "┐");
//            System.out.println("│" + centerText(title, totalWidth - 2) + "│");
//            System.out.println("├" + "─".repeat(widths[0] + 2) + "┼" + "─".repeat(widths[1] + 2) + "┼" +
//                    "─".repeat(widths[2] + 2) + "┼" + "─".repeat(widths[3] + 2) + "┤");
//
//            // Header
//            System.out.printf("│ %-" + widths[0] + "s │ %-" + widths[1] + "s │ %-" + widths[2] + "s │ %-" + widths[3] + "s │%n",
//                    "#", "NAME", "DESCRIPTION", "SONGS");
//
//            System.out.println("├" + "─".repeat(widths[0] + 2) + "┼" + "─".repeat(widths[1] + 2) + "┼" +
//                    "─".repeat(widths[2] + 2) + "┼" + "─".repeat(widths[3] + 2) + "┤");
//
//            // Rows
//            int index = 1;
//            for (JsonNode playlist : playlists) {
//                String name = playlist.has("name") ? playlist.get("name").asText() : "Unknown";
//                String desc = playlist.has("description") ? playlist.get("description").asText() : "No description";
//                int songCount = playlist.has("songIds") ? playlist.get("songIds").size() : 0;
//
//                System.out.printf("│ %-" + widths[0] + "d │ %-" + widths[1] + "s │ %-" + widths[2] + "s │ %-" + widths[3] + "d │%n",
//                        index++,
//                        truncate(name, widths[1]),
//                        truncate(desc, widths[2]),
//                        songCount);
//            }
//
//            System.out.println("└" + "─".repeat(widths[0] + 2) + "┴" + "─".repeat(widths[1] + 2) + "┴" +
//                    "─".repeat(widths[2] + 2) + "┴" + "─".repeat(widths[3] + 2) + "┘");
//            System.out.println("📊 Total: " + playlists.size() + " playlist(s)");
//        } catch (Exception e) {
//            printError("Failed to display playlists: " + e.getMessage());
//        }
//    }
//
//    private void handleViewPlaylistDetails() {
//        if (!validateSession()) return;
//        handleViewMyPlaylists();
//
//        printSectionHeader("🔍 PLAYLIST DETAILS");
//
//        try {
//            int playlistNumber = getValidatedIntegerInput("Enter playlist number: ", 1, Integer.MAX_VALUE);
//            String playlistId = getPlaylistIdByNumber(playlistNumber);
//
//            if (playlistId == null) {
//                printError("Invalid playlist number!");
//                return;
//            }
//
//            showLoading("Loading playlist details");
//            String result = playlistClient.getPlaylistWithSongs(playlistId);
//
//            try {
//                JsonNode data = objectMapper.readTree(result);
//
//                // Debug: Print raw response to understand structure
//                // System.out.println("DEBUG Raw response: " + data.toString());
//
//                JsonNode playlist = data;
//                String playlistName = "Unknown";
//                String description = "No description";
//                String owner = "Unknown";
//                JsonNode songs = null;
//
//                // Try different possible response structures
//                if (data.has("playlist")) {
//                    playlist = data.get("playlist");
//                }
//
//                if (playlist.has("name")) {
//                    playlistName = playlist.get("name").asText();
//                } else if (data.has("name")) {
//                    playlistName = data.get("name").asText();
//                }
//
//                if (playlist.has("description")) {
//                    description = playlist.get("description").asText();
//                } else if (data.has("description")) {
//                    description = data.get("description").asText();
//                }
//
//                if (playlist.has("username")) {
//                    owner = playlist.get("username").asText();
//                } else if (data.has("username")) {
//                    owner = data.get("username").asText();
//                } else if (playlist.has("userId")) {
//                    owner = playlist.get("userId").asText();
//                }
//
//                // Find songs in different possible locations
//                if (data.has("songs")) {
//                    songs = data.get("songs");
//                } else if (playlist.has("songs")) {
//                    songs = playlist.get("songs");
//                } else if (data.has("songIds")) {
//                    // If we only have song IDs, we might need to fetch song details separately
//                    printInfo("Note: Only song IDs available, details not loaded");
//                }
//
//                System.out.println("\n┌" + "─".repeat(78) + "┐");
//                System.out.println("│" + centerText("🎵 PLAYLIST DETAILS", 78) + "│");
//                System.out.println("├" + "─".repeat(78) + "┤");
//                System.out.println("│ 🎵  Name: " + String.format("%-65s", playlistName) + "│");
//                System.out.println("│ 📝  Description: " + String.format("%-58s", description) + "│");
//                System.out.println("│ 👤  Owner: " + String.format("%-65s", owner) + "│");
//                System.out.println("├" + "─".repeat(78) + "┤");
//
//                if (songs != null && songs.isArray() && songs.size() > 0) {
//                    printEnhancedSongsTable(songs, "🎶 SONGS IN PLAYLIST");
//                } else {
//                    System.out.println("│" + centerText("📭 This playlist is empty", 78) + "│");
//                    System.out.println("└" + "─".repeat(78) + "┘");
//                }
//            } catch (Exception e) {
//                printError("Failed to parse playlist data: " + e.getMessage());
//                // Print the actual response for debugging
//                System.out.println("Raw response: " + result);
//            }
//        } catch (Exception e) {
//            printError("Failed to load playlist details: " + e.getMessage());
//        }
//    }
//
//    private void handleAddSongToPlaylist() {
//        if (!validateSession()) return;
//        handleViewAllSongs();
//        System.out.println();
//        handleViewMyPlaylists();
//
//        printSectionHeader("➕ ADD SONG TO PLAYLIST");
//
//        try {
//            int playlistNumber = getValidatedIntegerInput("Enter playlist number: ", 1, Integer.MAX_VALUE);
//            int songNumber = getValidatedIntegerInput("Enter song number: ", 1, Integer.MAX_VALUE);
//
//            String playlistId = getPlaylistIdByNumber(playlistNumber);
//            String songId = getAllSongIdByNumber(songNumber);
//
//            if (playlistId == null || songId == null) {
//                printError("Invalid selection!");
//                return;
//            }
//
//            if (confirmAction("add this song to the playlist")) {
//                showLoading("Adding song to playlist");
//                String result = playlistClient.addSongToPlaylist(playlistId, songId, sessionManager.getToken());
//                parseAndDisplayResult(result, "Song added to playlist successfully!", null);
//            } else {
//                printInfo("Operation cancelled.");
//            }
//        } catch (Exception e) {
//            printError("Failed to add song: " + e.getMessage());
//        }
//    }
//
//    private void handleRemoveSongFromPlaylist() {
//        if (!validateSession()) return;
//        handleViewMyPlaylists();
//
//        printSectionHeader("➖ REMOVE SONG FROM PLAYLIST");
//
//        try {
//            int playlistNumber = getValidatedIntegerInput("Enter playlist number: ", 1, Integer.MAX_VALUE);
//            String playlistId = getPlaylistIdByNumber(playlistNumber);
//
//            if (playlistId == null) {
//                printError("Invalid playlist number!");
//                return;
//            }
//
//            // Show current playlist songs
//            System.out.println("\nCurrent playlist songs:");
//            handleViewPlaylistDetails();
//
//            int songNumber = getValidatedIntegerInput("\nEnter song number to remove: ", 1, Integer.MAX_VALUE);
//            String songId = getSongIdByNumber(songNumber);
//
//            if (songId == null) {
//                printError("Invalid song number!");
//                return;
//            }
//
//            if (confirmAction("remove this song from the playlist")) {
//                showLoading("Removing song from playlist");
//                String result = playlistClient.removeSongFromPlaylist(playlistId, songId, sessionManager.getToken());
//                parseAndDisplayResult(result, "Song removed from playlist successfully!", null);
//            } else {
//                printInfo("Operation cancelled.");
//            }
//        } catch (Exception e) {
//            printError("Failed to remove song: " + e.getMessage());
//        }
//    }
//
//    private void handleDeletePlaylist() {
//        if (!validateSession()) return;
//        handleViewMyPlaylists();
//
//        printSectionHeader("🗑️ DELETE PLAYLIST");
//
//        try {
//            int playlistNumber = getValidatedIntegerInput("Enter playlist number: ", 1, Integer.MAX_VALUE);
//            String playlistId = getPlaylistIdByNumber(playlistNumber);
//
//            if (playlistId == null) {
//                printError("Invalid playlist number!");
//                return;
//            }
//
//            if (confirmAction("permanently delete this playlist")) {
//                showLoading("Deleting playlist");
//                String result = playlistClient.deletePlaylist(playlistId, sessionManager.getToken());
//
//                if (result.contains("success") || result.contains("deleted")) {
//                    printSuccess("✅ Playlist deleted successfully!");
//                } else {
//                    printError("Failed to delete playlist: " + cleanErrorMessage(result));
//                }
//            } else {
//                printInfo("Deletion cancelled.");
//            }
//        } catch (Exception e) {
//            printError("Failed to delete playlist: " + e.getMessage());
//        }
//    }
//
//    private void exportMyData() {
//        if (!validateSession()) return;
//        printSectionHeader("💾 EXPORT MY DATA");
//
//        try {
//            showLoading("Collecting your data");
//
//            String songsResult = songClient.getMySongs(sessionManager.getToken());
//            String playlistsResult = playlistClient.getMyPlaylists(sessionManager.getToken());
//
//            JsonNode songs = objectMapper.readTree(songsResult);
//            JsonNode playlists = objectMapper.readTree(playlistsResult);
//
//            Map<String, Object> exportData = new LinkedHashMap<>();
//            exportData.put("user", sessionManager.getUsername());
//            exportData.put("exportedAt", new Date().toString());
//            exportData.put("songsCount", songs.size());
//            exportData.put("playlistsCount", playlists.size());
//            exportData.put("songs", songs);
//            exportData.put("playlists", playlists);
//
//            String exportJson = objectMapper.writerWithDefaultPrettyPrinter()
//                    .writeValueAsString(exportData);
//
//            String filename = "music_data_export_" + sessionManager.getUsername() + "_" +
//                    System.currentTimeMillis() + ".json";
//
//            Files.write(Paths.get(filename), exportJson.getBytes());
//            printSuccess("✅ Data exported successfully!");
//            System.out.println("📁 File: " + filename);
//            System.out.println("📊 Songs: " + songs.size() + ", Playlists: " + playlists.size());
//
//        } catch (Exception e) {
//            printError("Export failed: " + e.getMessage());
//        }
//    }
//
//    private void showHelp() {
//        System.out.println("\n" + "=".repeat(70));
//        System.out.println("                      HELP & TIPS");
//        System.out.println("=".repeat(70));
//        System.out.println("🎯 NAVIGATION:");
//        System.out.println("   • Use numbers to select menu options");
//        System.out.println("   • Press Enter to confirm inputs");
//        System.out.println("   • Use song/playlist numbers (not IDs)");
//        System.out.println("");
//        System.out.println("📝 INPUT TIPS:");
//        System.out.println("   • Username: 3-20 characters");
//        System.out.println("   • Password: At least 6 characters");
//        System.out.println("   • Email: Must be valid format");
//        System.out.println("   • Duration: In seconds (1-7200)");
//        System.out.println("");
//        System.out.println("🔍 SEARCH:");
//        System.out.println("   • Supports partial matching");
//        System.out.println("   • Case insensitive");
//        System.out.println("   • Try different keywords if no results");
//        System.out.println("");
//        System.out.println("💾 FEATURES:");
//        System.out.println("   • Export your data to JSON file");
//        System.out.println("   • View all songs in the system");
//        System.out.println("   • Create unlimited playlists");
//        System.out.println("=".repeat(70));
//        System.out.println("Press Enter to continue...");
//        scanner.nextLine();
//    }
//
//    // ==================== UTILITY METHODS ====================
//
//    private void printSectionHeader(String title) {
//        System.out.println("\n" + "🌟" + "─".repeat(50) + "🌟");
//        System.out.println("   " + title);
//        System.out.println("🌟" + "─".repeat(50) + "🌟");
//    }
//
//    private void printSuccess(String message) {
//        System.out.println("✅ " + message);
//    }
//
//    private void printError(String message) {
//        System.out.println("❌ " + message);
//    }
//
//    private void printInfo(String message) {
//        System.out.println("💡 " + message);
//    }
//
//    private void showLoading(String message) {
//        System.out.print("\n⏳ " + message);
//        for (int i = 0; i < 3; i++) {
//            System.out.print(".");
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//        System.out.println();
//    }
//
//    private String getValidatedInput(String prompt, String errorMessage, java.util.function.Predicate<String> validator) {
//        while (true) {
//            System.out.print(prompt);
//            String input = scanner.nextLine().trim();
//
//            if (validator.test(input)) {
//                return input;
//            }
//            printError(errorMessage);
//
//            System.out.print("🔄 Try again? (yes/no): ");
//            String choice = scanner.nextLine().trim().toLowerCase();
//            if (!choice.equals("yes") && !choice.equals("y")) {
//                throw new RuntimeException("Operation cancelled");
//            }
//        }
//    }
//
//    private int getValidatedIntegerInput(String prompt, int min, int max) {
//        while (true) {
//            System.out.print(prompt);
//            try {
//                int value = Integer.parseInt(scanner.nextLine().trim());
//                if (value >= min && value <= max) {
//                    return value;
//                }
//                printError("Please enter a number between " + min + " and " + max);
//            } catch (NumberFormatException e) {
//                printError("Please enter a valid number!");
//            }
//        }
//    }
//
//    private boolean confirmAction(String action) {
//        System.out.print("⚠️  Confirm " + action + "? (yes/no): ");
//        String confirmation = scanner.nextLine().trim().toLowerCase();
//        return confirmation.equals("yes") || confirmation.equals("y");
//    }
//
//    private boolean validateSession() {
//        if (!sessionManager.isLoggedIn()) {
//            printError("Session expired. Please login again.");
//            return false;
//        }
//        return true;
//    }
//
//    private <T> T withRetry(Supplier<T> operation, String operationName, int maxRetries) {
//        int attempts = 0;
//        while (attempts < maxRetries) {
//            try {
//                return operation.get();
//            } catch (Exception e) {
//                attempts++;
//                if (attempts == maxRetries) {
//                    printError("Failed to " + operationName + " after " + maxRetries + " attempts");
//                    throw new RuntimeException(operationName + " failed: " + e.getMessage());
//                }
//                System.out.println("🔄 Retry " + attempts + "/" + maxRetries + "...");
//                try {
//                    Thread.sleep(1000 * attempts);
//                } catch (InterruptedException ie) {
//                    Thread.currentThread().interrupt();
//                    throw new RuntimeException(ie);
//                }
//            }
//        }
//        return null;
//    }
//
//    private String cleanErrorMessage(String error) {
//        if (error == null) return "Unknown error";
//        return error.replaceAll("400 : \"", "")
//                .replaceAll("\"", "")
//                .replaceAll("\\{\"error\":\"", "")
//                .replaceAll("\"}", "")
//                .replaceAll(".*message.*:", "")
//                .trim();
//    }
//
//    private void parseAndDisplayResult(String result, String successMessage, String idLabel) {
//        try {
//            if (result == null || result.trim().isEmpty()) {
//                printError("No response from server!");
//                return;
//            }
//
//            JsonNode jsonNode = objectMapper.readTree(result);
//
//            if (jsonNode.has("id")) {
//                printSuccess(successMessage);
//                if (idLabel != null) {
//                    System.out.println("📋 " + idLabel + ": " + jsonNode.get("id").asText());
//                }
//            } else if (jsonNode.has("error")) {
//                printError("Error: " + cleanErrorMessage(jsonNode.get("error").asText()));
//            } else if (jsonNode.has("message")) {
//                printInfo(jsonNode.get("message").asText());
//            } else {
//                printSuccess(successMessage);
//            }
//        } catch (Exception e) {
//            printError("Failed to parse response: " + e.getMessage());
//        }
//    }
//
//    private boolean isValidEmail(String email) {
//        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
//    }
//
//    private String getSongIdByNumber(int number) {
//        try {
//            String result = songClient.getMySongs(sessionManager.getToken());
//            JsonNode songs = objectMapper.readTree(result);
//            if (songs.isArray() && number > 0 && number <= songs.size()) {
//                return songs.get(number - 1).get("id").asText();
//            }
//        } catch (Exception e) {
//            printError("Error fetching song: " + e.getMessage());
//        }
//        return null;
//    }
//
//    private String getAllSongIdByNumber(int number) {
//        try {
//            String result = songClient.getAllSongs();
//            JsonNode songs = objectMapper.readTree(result);
//            if (songs.isArray() && number > 0 && number <= songs.size()) {
//                return songs.get(number - 1).get("id").asText();
//            }
//        } catch (Exception e) {
//            printError("Error fetching song: " + e.getMessage());
//        }
//        return null;
//    }
//
//    private String getPlaylistIdByNumber(int number) {
//        try {
//            String result = playlistClient.getMyPlaylists(sessionManager.getToken());
//            JsonNode playlists = objectMapper.readTree(result);
//            if (playlists.isArray() && number > 0 && number <= playlists.size()) {
//                return playlists.get(number - 1).get("id").asText();
//            }
//        } catch (Exception e) {
//            printError("Error fetching playlist: " + e.getMessage());
//        }
//        return null;
//    }
//
//    private int[] calculateSongColumnWidths(JsonNode songs) {
//        int[] minWidths = {4, 20, 15, 15, 12, 8}; // #, TITLE, ARTIST, ALBUM, GENRE, DURATION
//        int[] maxWidths = {4, 30, 25, 25, 15, 8};
//        int[] widths = Arrays.copyOf(minWidths, minWidths.length);
//
//        for (JsonNode song : songs) {
//            widths[1] = Math.max(widths[1], Math.min(song.get("title").asText().length(), maxWidths[1]));
//            widths[2] = Math.max(widths[2], Math.min(song.get("artist").asText().length(), maxWidths[2]));
//            widths[3] = Math.max(widths[3], Math.min(song.get("album").asText().length(), maxWidths[3]));
//            widths[4] = Math.max(widths[4], Math.min(song.get("genre").asText().length(), maxWidths[4]));
//        }
//
//        return widths;
//    }
//
//    private String truncate(String str, int maxLength) {
//        if (str == null) return "";
//        if (str.length() <= maxLength) return str;
//        return str.substring(0, maxLength - 3) + "...";
//    }
//
//    private String formatDuration(int seconds) {
//        int minutes = seconds / 60;
//        int remainingSeconds = seconds % 60;
//        return String.format("%d:%02d", minutes, remainingSeconds);
//    }
//
//    private String centerText(String text, int width) {
//        if (text.length() >= width) return text.substring(0, width);
//        int padding = (width - text.length()) / 2;
//        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
//    }
//}