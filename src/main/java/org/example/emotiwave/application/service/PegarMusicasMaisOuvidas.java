package org.example.emotiwave.application.service;

import org.example.emotiwave.domain.entities.Musica;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.infra.repository.MusicaRepository;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.chrono.ChronoLocalDate;

@Service
public class PegarMusicasMaisOuvidas {


    private final SpotifyAuthService spotifyAuthService;
    private final MusicaRepository musicaRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public PegarMusicasMaisOuvidas(SpotifyAuthService spotifyAuthService, MusicaRepository musicaRepository) {
        this.spotifyAuthService = spotifyAuthService;
        this.musicaRepository = musicaRepository;
    }


    public ResponseEntity<String> pegarMusicasMaisOuvidas(Usuario usuario) {
        Instant now = Instant.now();

        if (usuario.getSpotify_info().getExpires_in().isBefore(now.plusSeconds(5 * 60))) {
            spotifyAuthService.refreshAccessToken(usuario);
        }

        String url = "https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=10";

        Musica musica = new Musica();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(usuario.getSpotify_info().getAcess_token());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        String.class);

        return response;
    }

}
