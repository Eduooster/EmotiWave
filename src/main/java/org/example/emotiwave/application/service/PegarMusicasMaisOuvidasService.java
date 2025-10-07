package org.example.emotiwave.application.service;

import jakarta.transaction.Transactional;
import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.in.TopMusicasUsuarioDto;
import org.example.emotiwave.application.mapper.MusicaMapper;
import org.example.emotiwave.domain.entities.*;
import org.example.emotiwave.infra.client.GeniusLyricsClient;
import org.example.emotiwave.infra.client.HuggingFaceZeroShotClient;
import org.example.emotiwave.infra.client.SpotifyClient;
import org.example.emotiwave.infra.repository.MusicaRepository;

import org.example.emotiwave.infra.repository.UsuarioMusicaRepository;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PegarMusicasMaisOuvidasService {



    private final MusicaRepository musicaRepository;
    private final MusicaMapper musicaMapper;
    private final SpotifyClient spotifyAuthService;
    private final GeniusLyricsClient geniusLyricsService;
    private final HuggingFaceZeroShotClient huggingFaceZeroShotService;
    private final UsuarioRepository usuarioRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final SpotifyClient spotifyClient;
    String url = "https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=10";
    private UsuarioMusicaRepository usuarioMusicaRepository;

    public PegarMusicasMaisOuvidasService(MusicaRepository musicaRepository, MusicaMapper musicaMapper, SpotifyClient spotifyAuthService, GeniusLyricsClient geniusLyricsService, HuggingFaceZeroShotClient huggingFaceZeroShotService, UsuarioRepository usuarioRepository, SpotifyClient spotifyClient, UsuarioMusicaRepository usuarioMusicaRepository) {
        this.musicaRepository = musicaRepository;
        this.musicaMapper = musicaMapper;
        this.spotifyAuthService = spotifyAuthService;
        this.geniusLyricsService = geniusLyricsService;
        this.huggingFaceZeroShotService = huggingFaceZeroShotService;
        this.usuarioRepository = usuarioRepository;
        this.spotifyClient = spotifyClient;
        this.usuarioMusicaRepository = usuarioMusicaRepository;
    }


    public ResponseEntity<List<MusicaSimplesDto>> pegarMusicasMaisOuvidas(Usuario usuario) {
        Instant now = Instant.now();

        //restaura o acess_token do usuario se precisar
        if (usuario.getSpotify_info().getExpiresIn().isBefore(now.plusSeconds(5 * 60))) {
            String newAccessToken = spotifyClient.refreshAccessToken(usuario);
            usuario.getSpotify_info().setAccessToken(newAccessToken);
            usuarioRepository.save(usuario);
        }
        TopMusicasUsuarioDto dtoSpotify  = montarRequestEPegarResponse(usuario,url);

        List<MusicaSimplesDto> topMusicas = topMusicasResponseToMusicaSimplesDto(dtoSpotify);

        converterTopMusicasToMusicaEntity(topMusicas,usuario);

        return ResponseEntity.ok(topMusicas);
    }

    private TopMusicasUsuarioDto montarRequestEPegarResponse(Usuario usuario, String url){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(usuario.getSpotify_info().getAccessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<TopMusicasUsuarioDto> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        TopMusicasUsuarioDto.class);

        return response.getBody();

    }

    private List<MusicaSimplesDto>  topMusicasResponseToMusicaSimplesDto(TopMusicasUsuarioDto dtoSpotify) {
        List<MusicaSimplesDto> topMusicas = new ArrayList<>();
        if (dtoSpotify != null && dtoSpotify.getItems() != null) {
            for (TopMusicasUsuarioDto.Track track : dtoSpotify.getItems()) {
                topMusicas.add(new MusicaSimplesDto(
                        track.getName(),
                        track.getArtists().get(0).getName(),
                        track.getId()

                ));
            }
            return topMusicas;
        }
        return topMusicas;
    }

    @Transactional
    protected void converterTopMusicasToMusicaEntity(List<MusicaSimplesDto> topMusicas, Usuario usuario) {
        for (MusicaSimplesDto topMusicaDto : topMusicas) {
            if (musicaRepository.findBySpotifyTrackId(topMusicaDto.getSpotifyTrackId()) != null) continue;

            try {
                Musica musicaEntity = musicaMapper.toEntity(topMusicaDto);
                String letra = geniusLyricsService.fetchLyrics(topMusicaDto.getArtista(), topMusicaDto.getTitulo());
                musicaEntity.setLetra(letra);
                musicaEntity.setSpotifyTrackId(topMusicaDto.getSpotifyTrackId());

                musicaRepository.save(musicaEntity);

                AnaliseMusica analise = huggingFaceZeroShotService.analisarScoreMusica(musicaEntity);
                analise.setMusica(musicaEntity);

                musicaEntity.setAnalise(analise);

                UsuarioMusica usuarioMusica = new UsuarioMusica();
                usuarioMusica.setMusica(musicaEntity);
                usuarioMusica.setUsuario(usuario);
                usuarioMusica.setSelecionada(false);
                usuarioMusica.setFonte(FonteMusica.SPOTIFY);
                usuarioMusicaRepository.save(usuarioMusica);


            } catch (IOException | InterruptedException e) {
                System.err.println("Erro processando música " + topMusicaDto.getTitulo() + ": " + e.getMessage());
            }
        }
    }








}
