package com.studymate.studymate.document.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiEmbeddingClient {
    private final RestClient restClient; //HTTP client used to communicate with Gemini
    private final String apiKey;
    private final String model;
    private final int dimensions;

    public GeminiEmbeddingClient(
            //@Value	Reads configuration values from application.properties
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.embedding.model:gemini-embedding-2}") String model, //default values
            @Value("${gemini.embedding.dimensions:768}") int dimensions)
    {
        this.apiKey=apiKey;
        this.model=model;
        this.dimensions=dimensions;
        //This creates an HTTP client. We use it to communicate with Google's API from our Spring Boot backend.
        this.restClient=RestClient.builder().baseUrl("https://generativelanguage.googleapis.com").build();
        //The base URL is Google's API address. You'll append the specific endpoint when sending a request.
    }

    public List<Double> generateDocumentEmbedding(String title, String content){ //content: the actual text of one document chunk.
        if(apiKey==null ||apiKey.isBlank()){
            throw new IllegalStateException("Gemini API key is not configured");
        }
        if (content==null||content.isBlank()){
            throw new IllegalStateException("Cannot generate embedding for empty content");
        }

        //title: document title | text: document content
        String formattedText= "title: "+(title==null? "none":title)+" |text: "+content;

//        It tells Gemini:
//        - Which text to convert into an embedding.
//        - How many dimensions the output should contain.
    //We are constructing a Java Map that represents the JSON request Google expects.

        Map<String, Object> requestBody = Map.of(
                "content", Map.of(
                        "parts", List.of(
                                Map.of("text", formattedText)
                        )
                ),
                "output_dimensionality", dimensions
        );

        try{
            //Sending the HTTP request
            JsonNode response=restClient.post() //http post request
                    .uri("/v1beta/models/{model}:embedContent",model)//api end point
                    .header("x-goog-api-key",apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody) //Attaches the JSON request body you constructed earlier
                    .retrieve()
                    .body(JsonNode.class); //Google sends a JSON response

            if(response==null){
                throw new IllegalStateException("Gemini returned an empty response");
            }
            //Extract the values
            //JsonNode.path: It lets you navigate through JSON objects by field name.
            JsonNode values= response.path("embedding").path("values");

            if(!values.isArray()){
                throw new IllegalStateException("Invalid embedding response from Gemini");
            }

            List<Double> embedding= new ArrayList<>();
            //Convert JSON numbers into a Java List
            for(JsonNode value:values){
                if(!value.isNumber()){
                    throw new IllegalStateException("Invalid value in Gemini embedding");
                }
                embedding.add(value.doubleValue()); //Convert it into a Java double
            }
            if(embedding.size()!=dimensions){
                throw new IllegalStateException("Unexpected embedding dimension: "+embedding.size());
            }
            return embedding;
        }catch (RestClientException e){
            throw new IllegalStateException("Gemini embedding request failed",e);
        }
    }

    public String getModel() {
        return model;
    }

    public int getDimensions() {
        return dimensions;
    }
}
