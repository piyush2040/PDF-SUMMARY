package com.PDF_Summary.service.ServiceLogic;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.PDF_Summary.service.Service.OpenAIFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAILogic implements OpenAIFactory {

    @Value("${spring.ai.openai.api-key}")
    private String API_KEY;

    private static final Logger logger = LoggerFactory.getLogger(OpenAILogic.class);
    private static final String SUMMARY_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public OpenAILogic() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String summarizeWithGPT(String text, List<Double> embedding, int characterLimit) throws IOException {
        try {
            logger.info("Summarize process started.");

            // Prepare your text (or replace this with actual text extraction from PDF)
            //String text = "Here is the text you want to summarize: [Text is in Vector ]" + embedding.toString(); // Use real text

            // Request payload for GPT summarization
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "gpt-3.5-turbo"); // Use newer models for summarization
            payload.put("messages", List.of(
                    Map.of("role", "system", "content", "You are a helpful assistant."),
                    Map.of("role", "user", "content", "Summarize the following text: " + text)
            ));
            payload.put("max_tokens", characterLimit); // Set token limit for the summary

            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(payload),
                    MediaType.parse("application/json")
            );

            // Build and execute the request
            Request request = new Request.Builder()
                    .url(SUMMARY_ENDPOINT)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Error occurred during summarization: {}", response);
                    throw new IOException("Unexpected response: " + response);
                }

                String responseBody = response.body().string();
                logger.info("Response received from OpenAI API.");

                // Parse the response body
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
                List<Map<String, Object>> choices = objectMapper.convertValue(responseMap.get("choices"), new TypeReference<List<Map<String, Object>>>() {});
                logger.info(choices.toString());
//                // Extract the summary from the response
//                String summary = (String) choices.get(0).get("message").get("content");
                Map<String, Object> choice = choices.get(0);  // This is now correctly a Map
                Map<String, Object> message = (Map<String, Object>) choice.get("message"); // Ensure it's a map
                String summary = (String) message.get("content"); // Now access the "content" field

                logger.info("Summarization completed." + summary);
                return summary;
            }

        } catch (IOException e) {
            logger.error("Error during API request: ", e);
            throw e;  // Rethrow or handle based on your requirement
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            return "ERROR|" + e.getMessage();
        }
    }
}
