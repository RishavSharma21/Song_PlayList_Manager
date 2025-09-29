package com.example.console.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${services.user.url}")
    private String userServiceUrl;

    public Map<String, String> register(String name, String username, String password, String email) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("name", name);
            request.put("username", username);
            request.put("password", password);
            request.put("email", email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    userServiceUrl + "/register",
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                Map<String, String> result = new HashMap<>();
                result.put("token", jsonNode.get("token").asText());
                result.put("username", jsonNode.get("username").asText());
                result.put("message", jsonNode.has("message") ? jsonNode.get("message").asText() : "Registration successful!");
                return result;
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Registration failed");
                return error;
            }
        } catch (HttpClientErrorException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getResponseBodyAsString());
            return error;
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Connection error: " + e.getMessage());
            return error;
        }
    }

    public Map<String, String> login(String username, String password) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("username", username);
            request.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    userServiceUrl + "/login",
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                Map<String, String> result = new HashMap<>();
                result.put("token", jsonNode.get("token").asText());
                result.put("username", jsonNode.get("username").asText());
                result.put("message", jsonNode.has("message") ? jsonNode.get("message").asText() : "Login successful!");
                return result;
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Login failed");
                return error;
            }
        } catch (HttpClientErrorException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getResponseBodyAsString());
            return error;
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Connection error: " + e.getMessage());
            return error;
        }
    }
}