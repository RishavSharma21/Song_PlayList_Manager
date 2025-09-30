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
        parseAndDisplayResult(result, "Song deleted successfully!", null);
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

        // Show playlist songs specifically for removal
        System.out.println("\nFetching playlist songs...");
        String result = playlistClient.getPlaylistWithSongs(playlistId);

        try {
            JsonNode data = objectMapper.readTree(result);
            if (data.has("playlist") && data.has("songs") && data.get("songs").isArray()) {
                JsonNode songs = data.get("songs");
                JsonNode playlist = data.get("playlist");

                if (songs.size() > 0) {
                    System.out.println("\n" + "=".repeat(80));
                    System.out.println("Playlist: " + playlist.get("name").asText());
                    System.out.println("Songs available for removal:");
                    System.out.println("=".repeat(80));

                    printSongsTable(songs, "SONGS IN PLAYLIST");

                    int songNumber = getValidInteger("Enter song number to remove: ", 1, songs.size());
                    String songId = getPlaylistSongIdByNumber(playlistId, songNumber);

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
                    String removeResult = playlistClient.removeSongFromPlaylist(playlistId, songId, sessionManager.getToken());
                    parseAndDisplayResult(removeResult, "Song removed from playlist!", null);
                } else {
                    System.out.println("\nThis playlist is empty. No songs to remove.");
                }
            } else {
                System.out.println("Could not fetch playlist details.");
            }
        } catch (Exception e) {
            System.out.println("Error fetching playlist: " + e.getMessage());
        }
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
        parseAndDisplayResult(result, "Playlist deleted successfully!", null);
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

    private String getPlaylistSongIdByNumber(String playlistId, int number) {
        try {
            String result = playlistClient.getPlaylistWithSongs(playlistId);
            JsonNode data = objectMapper.readTree(result);

            if (data.has("songs") && data.get("songs").isArray()) {
                JsonNode songs = data.get("songs");
                if (number > 0 && number <= songs.size()) {
                    return songs.get(number - 1).get("id").asText();
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching playlist song: " + e.getMessage());
        }
        return null;
    }

    private void parseAndDisplayResult(String result, String successMessage, String idLabel) {
        try {
            // First, try to clean the response if it contains malformed JSON
            String cleanedResult = result.replaceAll("400 : \"", "")
                    .replaceAll("\"}", "}")
                    .replaceAll("\"$", "");

            JsonNode jsonNode = objectMapper.readTree(cleanedResult);

            if (jsonNode.has("id") || jsonNode.has("message") || isSuccessResponse(result)) {
                System.out.println(successMessage);
                if (idLabel != null && jsonNode.has("id")) {
                    System.out.println(idLabel + ": " + jsonNode.get("id").asText());
                }
            } else if (jsonNode.has("error")) {
                String error = jsonNode.get("error").asText();
                System.out.println("Error: " + error);
            } else {
                // If we can't parse properly but it seems successful
                if (isSuccessResponse(result)) {
                    System.out.println(successMessage);
                } else {
                    System.out.println("Response: " + result);
                }
            }
        } catch (Exception e) {
            // If JSON parsing fails entirely, show the raw response
            if (isSuccessResponse(result)) {
                System.out.println(successMessage);
            } else if (result.contains("error") || result.contains("failed")) {
                System.out.println("Error: " + result);
            } else {
                System.out.println("Response: " + result);
            }
        }
    }

    private boolean isSuccessResponse(String response) {
        if (response == null) return false;
        String lowerResponse = response.toLowerCase();
        return lowerResponse.contains("success") ||
                lowerResponse.contains("created") ||
                lowerResponse.contains("added") ||
                lowerResponse.contains("updated") ||
                lowerResponse.contains("deleted") ||
                lowerResponse.contains("\"id\"");
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

