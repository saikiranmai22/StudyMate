package com.studymate.studymate.document.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;

import java.util.Map;

@Component
public class GeminiChatClient {
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiChatClient(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.chat.model}") String model
    ){
        this.apiKey=apiKey;
        this.model=model;
        //Creates an HTTP client that your Spring Boot application uses to communicate with Google's API
        this.restClient=RestClient.builder().baseUrl("https://generativelanguage.googleapis.com/v1beta").build();
    }

    public String generateAnswer(String prompt){
        Map<String,Object> requestBody= Map.of(
                "model", model,
                "input", prompt,
                "system_instruction", "You are StudyMate, a helpful educational assistant. " +
                        "Answer accurately using the provided document context."
        );

        try{
            JsonNode response= restClient.post()
                    .uri("/interactions")
                    .header("x-goog-api-key",apiKey) //The API key is sent through an HTTP header. It is never exposed to React.
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            if(response==null){
                throw new IllegalStateException("Empty response received from Gemini");
            }
            JsonNode steps= response.path("steps");
            for(JsonNode step:steps){
                if("model_output".equals(step.path("type").asText())){
                    JsonNode contents= step.path("content");
                    StringBuilder answer= new StringBuilder();

                    for(JsonNode content: contents){
                        if("text".equals(content.path("type").asText())){
                            answer.append(content.path("text").asText());
                        }
                    }
                    if(!answer.isEmpty()){
                        return answer.toString();
                    }
                }
            }
            throw new IllegalStateException("Gemini did not return generated text");

        }catch(RestClientResponseException ex){
            throw new IllegalStateException("Gemini API request failed with status "+ ex.getStatusCode().value(),ex);
        }
    }
}
