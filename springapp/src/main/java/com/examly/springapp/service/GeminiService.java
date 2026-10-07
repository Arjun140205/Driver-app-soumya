package com.examly.springapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Thin wrapper around the Google Gemini REST API (embeddings + text generation).
 * Every method degrades gracefully: when no API key is configured, or a call fails,
 * embed() / generate() return null and the callers fall back to non-AI logic.
 */
@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.embedding.model:text-embedding-004}")
    private String embeddingModel;

    @Value("${gemini.generation.model:gemini-2.0-flash}")
    private String generationModel;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public boolean isEnabled() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    /** Returns the embedding vector for the text, or null when AI is disabled / the call fails. */
    public float[] embed(String text) {
        if (!isEnabled() || text == null || text.isBlank()) {
            return null;
        }
        try {
            Map<String, Object> body = Map.of("content", Map.of("parts", List.of(Map.of("text", text))));
            JsonNode values = post(embeddingModel, "embedContent", body).path("embedding").path("values");
            if (!values.isArray() || values.size() == 0) {
                return null;
            }
            float[] vector = new float[values.size()];
            for (int i = 0; i < vector.length; i++) {
                vector[i] = (float) values.get(i).asDouble();
            }
            return vector;
        } catch (Exception e) {
            log.warn("Gemini embedding failed: {}", e.getMessage());
            return null;
        }
    }

    /** Returns the generated text, or null when AI is disabled / the call fails. */
    public String generate(String prompt) {
        return generate(prompt, false);
    }

    /** Same as generate(prompt) but asks Gemini to answer with raw JSON. */
    public String generateJson(String prompt) {
        return generate(prompt, true);
    }

    private String generate(String prompt, boolean json) {
        if (!isEnabled() || prompt == null || prompt.isBlank()) {
            return null;
        }
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));
            Map<String, Object> config = new LinkedHashMap<>();
            config.put("temperature", 0.2);
            if (json) {
                config.put("responseMimeType", "application/json");
            }
            body.put("generationConfig", config);
            JsonNode text = post(generationModel, "generateContent", body)
                    .path("candidates").path(0).path("content").path("parts").path(0).path("text");
            return text.isMissingNode() ? null : text.asText();
        } catch (Exception e) {
            log.warn("Gemini generation failed: {}", e.getMessage());
            return null;
        }
    }

    private JsonNode post(String model, String method, Object body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + model + ":" + method))
                .timeout(Duration.ofSeconds(25))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey.trim())
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("Gemini API returned HTTP " + response.statusCode());
        }
        return mapper.readTree(response.body());
    }
}
