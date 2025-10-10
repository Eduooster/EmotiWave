package org.example.emotiwave.infra.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.example.emotiwave.application.dto.out.AcessTokenResponseDto;
import org.example.emotiwave.domain.entities.Usuario;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

@Component
public class SpotifyClient {


    private final ObjectMapper objectMapper;
    String clientId = System.getenv("CLIENT_ID_SPOTIFY");
    private final String redirectUri = "http://127.0.0.1:8080/spotify/callback";
    private final String scopes = "user-top-read user-read-private user-read-recently-played";
    private final String baseUrl = "https://accounts.spotify.com/authorize";
    String secret = System.getenv("SECRET_SPOTIFY");
    private final RestTemplate restTemplate = new RestTemplate();
    private final WebClient webClient;



    public SpotifyClient(ObjectMapper objectMapper, WebClient webClient) {
        this.objectMapper = objectMapper;

        this.webClient = webClient;
    }

    public String construirAutorizacao(String authHeader) {
        // remove "Bearer " do header
        String jwt = authHeader.replace("Bearer ", "");

        // constrói a URL de forma segura
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", scopes)
                .queryParam("state", jwt)
                .build()
                .toUriString();

        return url;
    }

    public AcessTokenResponseDto exchangeCodeForTokens(String code) {
        try {
            String basicAuth = Base64.getEncoder()
                    .encodeToString((clientId + ":" + secret).getBytes(StandardCharsets.UTF_8));

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("code", code);
            formData.add("redirect_uri", redirectUri);

            WebClient webClient = WebClient.builder()
                    .baseUrl("https://accounts.spotify.com")
                    .build();

            return webClient.post()
                    .uri("/api/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(AcessTokenResponseDto.class)
                    .block(); // bloqueia até receber a resposta
        } catch (Exception e) {
            throw new RuntimeException("Erro ao trocar código por tokens do Spotify", e);
        }
    }

    public AcessTokenResponseDto refreshAccessToken(Usuario usuario) {
        try {
            String refreshToken = usuario.getSpotify_info().getRefreshToken();

            String basicAuth = Base64.getEncoder()
                    .encodeToString((clientId + ":" + secret).getBytes(StandardCharsets.UTF_8));

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);

            return webClient.post()
                    .uri("/api/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(AcessTokenResponseDto.class)
                    .block(); // bloqueia até receber a resposta

        } catch (Exception e) {
            throw new RuntimeException("Erro ao renovar access token do Spotify", e);
        }
    }


    public <T> T enviarRequisicaoSpotifyUtils(
            Usuario usuario,
            String url,
            ParameterizedTypeReference<T> responseType,
            @Nullable Long after // use Long para timestamps em ms
    ) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(usuario.getSpotify_info().getAccessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (after != null) {
            uriBuilder.queryParam("after", after);
        }
        String urlFinal = uriBuilder.toUriString();

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    urlFinal,
                    HttpMethod.GET,
                    request,
                    responseType
            );
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Falha ao consultar Spotify: " + e.getStatusCode());
        }
    }


}


//    public MusicasUsuarioSpotifyDto pegarMusicasOuvidasDoDia() {
//
//    }


