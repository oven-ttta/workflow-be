package com.parttimestudent.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.parttimestudent.dto.TimetableResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient;
    private final Gson gson;

    public GeminiService() {
        this.webClient = WebClient.builder().build();
        this.gson = new Gson();
    }

    public TimetableResponse extractTimetableFromImage(MultipartFile file) throws IOException {
        // Convert image to base64
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String mimeType = file.getContentType();

        // Create prompt for Gemini
        String prompt = """
            Please analyze this timetable image and extract the schedule information.
            For each time slot, identify:
            - Day of week (Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday)
            - Start time (in HH:mm format, 24-hour)
            - End time (in HH:mm format, 24-hour)
            - Subject name (if it's a class) or "Free" if it's free time
            - Whether it's free time (true) or class time (false)

            Return the data as a JSON array with this exact structure:
            {
              "slots": [
                {
                  "dayOfWeek": "Monday",
                  "startTime": "09:00",
                  "endTime": "10:30",
                  "subject": "Mathematics",
                  "isFree": false
                },
                {
                  "dayOfWeek": "Monday",
                  "startTime": "10:30",
                  "endTime": "12:00",
                  "subject": "Free",
                  "isFree": true
                }
              ]
            }

            Important: Return ONLY the JSON data, no additional text or explanation.
            """;

        // Build request body for Gemini API
        JsonObject requestBody = buildGeminiRequest(prompt, base64Image, mimeType);

        try {
            // Call Gemini 2.5 Flash API
            String response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse response
            return parseGeminiResponse(response);

        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();

            // Return empty timetable if error occurs
            TimetableResponse emptyResponse = new TimetableResponse();
            emptyResponse.setSlots(new ArrayList<>());
            return emptyResponse;
        }
    }

    private JsonObject buildGeminiRequest(String prompt, String base64Image, String mimeType) {
        JsonObject request = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();

        // Add text part
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);
        parts.add(textPart);

        // Add image part (inline data)
        JsonObject imagePart = new JsonObject();
        JsonObject inlineData = new JsonObject();
        inlineData.addProperty("mime_type", mimeType);
        inlineData.addProperty("data", base64Image);
        imagePart.add("inline_data", inlineData);
        parts.add(imagePart);

        content.add("parts", parts);
        contents.add(content);
        request.add("contents", contents);

        return request;
    }

    private TimetableResponse parseGeminiResponse(String response) {
        try {
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");

            if (candidates != null && candidates.size() > 0) {
                JsonObject candidate = candidates.get(0).getAsJsonObject();
                JsonObject content = candidate.getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");

                if (parts != null && parts.size() > 0) {
                    String text = parts.get(0).getAsJsonObject().get("text").getAsString();

                    // Clean up the response (remove markdown code blocks if present)
                    text = text.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

                    // Parse the timetable JSON
                    return gson.fromJson(text, TimetableResponse.class);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            e.printStackTrace();
        }

        // Return empty timetable if parsing fails
        TimetableResponse emptyResponse = new TimetableResponse();
        emptyResponse.setSlots(new ArrayList<>());
        return emptyResponse;
    }
}
