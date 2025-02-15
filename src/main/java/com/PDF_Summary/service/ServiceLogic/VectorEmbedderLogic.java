package com.PDF_Summary.service.ServiceLogic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.PDF_Summary.service.Service.VectorEmbedderFactory;

@Service
public class VectorEmbedderLogic implements VectorEmbedderFactory {
	private static final Logger logger = LoggerFactory.getLogger(VectorEmbedderLogic.class);
    @Value("${spring.ai.openai.api-key}")
    private String API_KEY;

    private static final String EMBEDDING_ENDPOINT = "https://api.openai.com/v1/embeddings";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public VectorEmbedderLogic() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Double> GenerateVector(String text) throws Exception {
    	logger.info("VECTOR EMBEDDING START");
        // Create request payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "text-embedding-ada-002");
        payload.put("input", text);

        RequestBody body = RequestBody.create(
            objectMapper.writeValueAsString(payload),
            MediaType.parse("application/json")
        );

        // Build the request
        Request request = new Request.Builder()
            .url(EMBEDDING_ENDPOINT)
            .post(body)
            .addHeader("Authorization", "Bearer " + API_KEY)
            .addHeader("Content-Type", "application/json")
            .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
            	logger.error("VECTOR EMBEDDING error in call response");
                throw new IOException("ERROR|Unexpected response: " + response);
            }
            logger.info("VECTOR EMBEDDING RESPONSE CAME");
            // Read the response body as a string
            String responseBody = response.body().string();

            if (responseBody == null || responseBody.isEmpty()) {
            	logger.error("VECTOR EMBEDDING error in call response null");
                throw new IOException("ERROR|Empty response body");
            }

            // Parse the response body with TypeReference to specify the generic type
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            
            // Deserialize the "data" field to the correct type
            List<Map<String, Object>> data = objectMapper.readValue(objectMapper.writeValueAsString(responseMap.get("data")), new TypeReference<List<Map<String, Object>>>() {});
            
            // Safely extract the embedding data
            List<Double> embedding = objectMapper.readValue(objectMapper.writeValueAsString(data.get(0).get("embedding")), new TypeReference<List<Double>>() {});
            logger.info(embedding.toString());
            return embedding;
        }
        
    }
}
