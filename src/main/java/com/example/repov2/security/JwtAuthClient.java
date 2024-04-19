package com.example.repov2.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;



public class JwtAuthClient {



    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Replace these values with your actual auth server URL and credentials
        String authServerUrl = "https://iam.dms.dev.usw2.ficoanalyticcloud.com/realms/FicoPlatformRealm/protocol/openid-connect/token";
        String clientId = "repo-admin";
        String clientSecret = "8uUAs5uBeslx7fAlrYTZdPK3dfaWAqSK";

        // Build the request body with client credentials
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        // Build the POST request to the auth server
        Request request = new Request.Builder()
                .url(authServerUrl)
                .post(formBody)
                .build();

        // Execute the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            // Parse the response body to extract the token
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String tokenResponse = responseBody.string();
                // You can parse the JSON response to extract the token
                //System.out.println("Access Token: " + fetchToken(tokenResponse));
            }
        }
    }

    public static String fetchToken(String jsonResponse) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonResponse);
        String accessToken = jsonNode.get("access_token").asText();
        return accessToken;
    }
}
