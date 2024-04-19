package com.example.repov2.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AuthTokenUtil {


  String AUTH_SERVER_URL =
      "https://iam.dms.dev.usw2.ficoanalyticcloud.com/realms/FicoPlatformRealm/protocol/openid-connect/token";
  String CLIENT_ID = "repo-admin";
  String CLIENT_SECRET = "8uUAs5uBeslx7fAlrYTZdPK3dfaWAqSK";

  public String fetchToken() throws IOException {
    OkHttpClient client = new OkHttpClient();

    // Replace these values with your actual auth server URL and credentials


    // Build the request body with client credentials
    RequestBody formBody = new FormBody.Builder()
        .add("grant_type", "client_credentials")
        .add("client_id", CLIENT_ID)
        .add("client_secret", CLIENT_SECRET)
        .build();

    // Build the POST request to the auth server
    Request request = new Request.Builder()
        .url(AUTH_SERVER_URL)
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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(tokenResponse);
        String accessToken = jsonNode.get("access_token").asText();

        //System.out.println("Access Token: " + accessToken);
        return accessToken;

      }
      return null;
    }
  }
}