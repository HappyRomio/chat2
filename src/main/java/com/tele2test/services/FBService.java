package com.tele2test.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class FBService {

    private final RestTemplate restTemplate;

    public FBService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public UserProfile getProfile(String id, String accessToken) {
        try {
            //params
            String params = "fields=id,name,email,first_name,last_name&access_token=" + accessToken;

            //build url
            String url = "https://graph.facebook.com/v3.2/" + id + "?" + params;

            //create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // create request
            HttpEntity request = new HttpEntity(headers);

            //use rest template
            ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            //check for status code
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = new ObjectMapper().readTree(response.getBody());

                // return a user profile object
                return new UserProfile(root.path("id").asText(), root.path("name").asText(), root.path("first_name").asText(),
                        root.path("last_name").asText(), root.path("email").asText(), null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}