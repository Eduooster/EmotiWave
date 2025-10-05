package org.example.emotiwave.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.emotiwave.application.dto.out.AcessTokenResponseDto;
import org.example.emotiwave.domain.entities.Usuario;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class SpotifyClient {


    private final ObjectMapper objectMapper;
    String clientId = System.getenv("CLIENT_ID_SPOTIFY");
    private final String redirectUri = "http://127.0.0.1:8080/spotify/callback";
    private final String scopes = "user-top-read user-read-private";
    private final String baseUrl = "https://accounts.spotify.com/authorize";
    String secret = System.getenv("SECRET_SPOTIFY");


    public SpotifyClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

    }

    public String contruirAutorizacao(String authHeader) {
        String jwt = authHeader.replace("Bearer ", "");

        return baseUrl + "?" +
                "client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scopes, StandardCharsets.UTF_8) +
                "&state=" + jwt;
    }


    public AcessTokenResponseDto exchangeCodeForTokens(String code, Usuario usuario) {
        try {
            String url = "https://accounts.spotify.com/api/token";

            String body = "grant_type=authorization_code"
                    + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String basicAuth = Base64.getEncoder()
                    .encodeToString((clientId + ":" + secret).getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            InputStream inputStream;
            if (conn.getResponseCode() >= 400) {
                inputStream = conn.getErrorStream();
                if (inputStream == null) {
                    throw new RuntimeException("Spotify retornou erro " + conn.getResponseCode() + " sem corpo");
                }
            } else {
                inputStream = conn.getInputStream();
            }

            String response = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines().reduce("", (acc, line) -> acc + line);

            AcessTokenResponseDto acessTokenResponse = objectMapper.readValue(response, AcessTokenResponseDto.class);
            return acessTokenResponse;


        } catch (Exception e) {
            throw new RuntimeException("Erro ao trocar code por token no Spotify", e);
        }
    }


    public String refreshAccessToken(Usuario usuario) {
        try {
            String refreshToken = usuario.getSpotify_info().getRefreshToken();
            String url = "https://accounts.spotify.com/api/token";

            String body = "grant_type=refresh_token&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String basicAuth = Base64.getEncoder()
                    .encodeToString((clientId + ":" + secret).getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = br.lines().reduce("", (acc, line) -> acc + line);

            Map<String, Object> tokens = new ObjectMapper().readValue(response, new TypeReference<>() {});
            String newAccessToken = (String) tokens.get("access_token");



            return newAccessToken;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao renovar access token do Spotify", e);
        }
    }


}

