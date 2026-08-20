package com.prepiq.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model}")
    private String primaryModel;

    @Value("${gemini.api.fallback-model}")
    private String fallbackModel;

    public GeminiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    public String generateContent(String prompt) {
        // Try primary model twice
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                return callGemini(prompt, primaryModel);
            } catch (Exception e) {
                System.out.println("Primary model attempt " + attempt + " failed: " + e.getMessage());
                sleep(1000L * attempt);
            }
        }

        // Fall back to lite model, try twice
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                return callGemini(prompt, fallbackModel);
            } catch (Exception e) {
                System.out.println("Fallback model attempt " + attempt + " failed: " + e.getMessage());
                sleep(1000L * attempt);
            }
        }

        throw new RuntimeException("AI service is experiencing high demand on Google's servers. Please try again in a minute.");
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private String callGemini(String prompt, String modelName) throws Exception {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        String responseJson = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/{model}:generateContent")
                        .queryParam("key", apiKey)
                        .build(modelName))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode root = objectMapper.readTree(responseJson);
        return root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
    }
}