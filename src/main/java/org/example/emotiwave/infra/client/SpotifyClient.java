package org.example.emotiwave.infra.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.example.emotiwave.application.dto.out.AccessTokenResponseDto;

import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.exceptions.FalhaAoPegarTokenAcess;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;


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

        String jwt = authHeader.replace("Bearer ", "");


        return  UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", scopes)
                .queryParam("state", jwt)
                .build()
                .toUriString();

    }

    public AccessTokenResponseDto exchangeCodeForTokens(String code) {
        try {
            String basicAuth = Base64.getEncoder()
                    .encodeToString((clientId + ":" + secret).getBytes(StandardCharsets.UTF_8));

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("code", code);
            formData.add("redirect_uri", redirectUri);

            WebClient webClient = WebClient.builder()
                    .baseUrl("https://accounts.spotify.com")
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configurer -> configurer.defaultCodecs()
                                    .maxInMemorySize(Integer.MAX_VALUE))
                            .build())
                    .build();

            return webClient.post()
                    .uri("/api/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(AccessTokenResponseDto.class)
                    .block();
        } catch (FalhaAoPegarTokenAcess e) {
            throw new RuntimeException("Erro ao trocar código por tokens do Spotify", e);
        }
    }

    public AccessTokenResponseDto refreshAccessToken(Usuario usuario) {
        try {
            String refreshToken = usuario.getSpotifyInfo().getRefreshToken();
            String basicAuth = Base64.getEncoder()
                    .encodeToString((clientId + ":" + secret).getBytes(StandardCharsets.UTF_8));


            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));


            return webClient.post()
                    .uri("https://accounts.spotify.com/api/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(AccessTokenResponseDto.class)
                    .block();


        } catch (Exception e) {
            throw new RuntimeException("Erro ao renovar access token do Spotify", e);
        }
    }

    public <T> T enviarRequisicaoSpotifyUtils(
            Usuario usuario,
            String url,
            ParameterizedTypeReference<T> responseType,
            @Nullable Long after

    ) {
        try {
            String urlFinal = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParamIfPresent("after", Optional.ofNullable(after))
                    .toUriString();


            return webClient.get()
                    .uri(urlFinal)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + usuario.getSpotifyInfo().getAccessToken())
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();

        } catch (WebClientResponseException e) {
            throw new RuntimeException("Falha ao consultar Spotify: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao chamar a API do Spotify", e);
        }

    }}


