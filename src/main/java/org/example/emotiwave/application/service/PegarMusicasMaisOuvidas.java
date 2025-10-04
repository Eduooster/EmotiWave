package org.example.emotiwave.application.service;

import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.in.TopMusicasUsuarioDto;
import org.example.emotiwave.application.mapper.MusicaMapper;
import org.example.emotiwave.domain.entities.Musica;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.infra.repository.MusicaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PegarMusicasMaisOuvidas {



    private final MusicaRepository musicaRepository;
    private final MusicaMapper musicaMapper;
    private final SpotifyAuthService spotifyAuthService;
    private final GeniusLyricsService geniusLyricsService;

    private final RestTemplate restTemplate = new RestTemplate();

    public PegarMusicasMaisOuvidas(MusicaRepository musicaRepository, MusicaMapper musicaMapper, SpotifyAuthService spotifyAuthService, GeniusLyricsService geniusLyricsService) {
        this.musicaRepository = musicaRepository;
        this.musicaMapper = musicaMapper;
        this.spotifyAuthService = spotifyAuthService;

        this.geniusLyricsService = geniusLyricsService;
    }


    public ResponseEntity<List<MusicaSimplesDto>> pegarMusicasMaisOuvidas(Usuario usuario) {
        Instant now = Instant.now();

        if (usuario.getSpotify_info().getExpires_in().isBefore(now.plusSeconds(5 * 60))) {
            spotifyAuthService.refreshAccessToken(usuario);
        }

        String url = "https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=10";

        Musica musica = new Musica();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(usuario.getSpotify_info().getAcess_token());



        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<TopMusicasUsuarioDto> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        TopMusicasUsuarioDto.class);
        List<MusicaSimplesDto> topMusicas = new ArrayList<>();
        TopMusicasUsuarioDto dtoSpotify = response.getBody();

        if (dtoSpotify != null && dtoSpotify.getItems() != null) {
            for (TopMusicasUsuarioDto.Track track : dtoSpotify.getItems()) {
                topMusicas.add(new MusicaSimplesDto(
                        track.getName(),
                        track.getArtists().get(0).getName(),
                        track.getId()
                ));
            }
        }

        topMusicas.forEach(topMusica -> {
            // Verifica se a música já existe
            Musica existente = musicaRepository.findBySpotifyTrackId(topMusica.getSpotify_track_id());
            if (existente != null) return;

            // Busca a letra
            String letra = fetchLyricsSafe(topMusica.getArtista(), topMusica.getTitulo());

            // Cria e salva a música
            Musica musicaEntity = new Musica();
            musicaEntity.setTitulo(topMusica.getTitulo());
            musicaEntity.setArtista(topMusica.getArtista());
            musicaEntity.setSpotifyTrackId(topMusica.getSpotify_track_id());
            musicaEntity.setLetra(letra);

            musicaRepository.save(musicaEntity);
        }
        );
        return ResponseEntity.ok(topMusicas);
    }

    public String fetchLyricsSafe(String artista, String titulo) {
        try {
            return geniusLyricsService.fetchLyrics(artista, titulo);
        } catch (IOException | InterruptedException e)
            System.err.println("Erro ao buscar letra de " + titulo + " de " + artista + ": " + e.getMessage());
            return "";
        }
    }


}
