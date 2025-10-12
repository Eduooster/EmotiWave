package org.example.emotiwave.application.service.spotifyServices;

import jakarta.transaction.Transactional;
import org.example.emotiwave.application.dto.in.GeneroMusicaSpotifyDto;
import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.in.MusicasUsuarioSpotifyDto;

import org.example.emotiwave.application.mapper.MusicaMapper;
import org.example.emotiwave.application.service.GeniusLyricsService;
import org.example.emotiwave.application.service.HuggingFaceZeroShotService;
import org.example.emotiwave.domain.entities.*;
import org.example.emotiwave.infra.client.SpotifyClient;
import org.example.emotiwave.infra.repository.MusicaRepository;

import org.example.emotiwave.infra.repository.UsuarioMusicaRepository;
import org.example.emotiwave.infra.repository.UsuarioRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.core.ParameterizedTypeReference;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TopMusicasSpotifyService {

    private final MusicaRepository musicaRepository;
    private final MusicaMapper musicaMapper;
    private final SpotifyService spotifyService;
    private final GeniusLyricsService geniusLyricsService;
    private final HuggingFaceZeroShotService huggingFaceZeroShotService;
    String SPOTIFY_TOP_TRACKS_URL = "https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=3";
    String SPOTIFY_ARTIST_URL = "https://api.spotify.com/v1/artists/";
    private UsuarioMusicaRepository usuarioMusicaRepository;
    Instant now = Instant.now();


    public TopMusicasSpotifyService(MusicaRepository musicaRepository, MusicaMapper musicaMapper, SpotifyService spotifyService, GeniusLyricsService geniusLyricsService, HuggingFaceZeroShotService huggingFaceZeroShotService, UsuarioRepository usuarioRepository, SpotifyClient spotifyClient, UsuarioMusicaRepository usuarioMusicaRepository) {
        this.musicaRepository = musicaRepository;
        this.musicaMapper = musicaMapper;
        this.spotifyService = spotifyService;
        this.geniusLyricsService = geniusLyricsService;
        this.huggingFaceZeroShotService = huggingFaceZeroShotService;
        this.usuarioMusicaRepository = usuarioMusicaRepository;
    }


    public List<MusicaSimplesDto> buscarTopMusicasSpotify(Usuario usuario) {

        spotifyService.verificarExpiracaoToken(usuario);

        MusicasUsuarioSpotifyDto dtoSpotify  = spotifyService.enviarRequisicaoSpotifyUtilsV2(usuario,
                SPOTIFY_TOP_TRACKS_URL,
                new ParameterizedTypeReference<>() {},null );


         List<MusicaSimplesDto> topMusicas = converterTopMusicasParaDto(dtoSpotify,usuario);

        converterTopMusicasParaEntidade(topMusicas,usuario);

        return topMusicas;
    }



    private List<MusicaSimplesDto> converterTopMusicasParaDto(MusicasUsuarioSpotifyDto dtoSpotify, Usuario usuario) {

        List<MusicaSimplesDto> topMusicas = new ArrayList<>();
        if (dtoSpotify != null && dtoSpotify.getItems() != null) {

            for (MusicasUsuarioSpotifyDto.Track track : dtoSpotify.getItems()) {

                if (track.getArtists() != null && !track.getArtists().isEmpty()) {
                    String artistaId = track.getArtists().get(0).getId();

                    GeneroMusicaSpotifyDto genero = spotifyService.getGeneros(
                            track.getArtistsIds(),
                            usuario,
                            new ParameterizedTypeReference<>() {},
                            null,
                            SPOTIFY_ARTIST_URL
                    );


                    String generoString = (genero != null && !genero.genres().isEmpty())
                            ? genero.genres().get(0)
                            : "Desconhecido";

                    topMusicas.add(new MusicaSimplesDto(
                            track.getName(),
                            track.getArtistsNames(),
                            track.getId(),
                            artistaId,
                            generoString
                    ));
                }
            }
        }
        return topMusicas;
    }

    @Transactional
    protected void converterTopMusicasParaEntidade(List<MusicaSimplesDto> topMusicas, Usuario usuario) {
        for (MusicaSimplesDto topMusicaDto : topMusicas) {
            if (musicaRepository.findBySpotifyTrackId(topMusicaDto.getSpotifyTrackId()) != null) continue;

            try {
                Musica musicaEntity = musicaMapper.toEntity(topMusicaDto);
                String letra = geniusLyricsService.buscarLyrics(topMusicaDto.getArtista(), topMusicaDto.getTitulo());
                musicaEntity.setLetra(letra);
                musicaEntity.setSpotifyTrackId(topMusicaDto.getSpotifyTrackId());
                musicaEntity.setGenero(String.valueOf(topMusicaDto.getGenero()));

                musicaRepository.save(musicaEntity);

                AnaliseMusica analise = huggingFaceZeroShotService.analisarMusica(musicaEntity);
                analise.setMusica(musicaEntity);

                musicaEntity.setAnalise(analise);

                UsuarioMusica usuarioMusica = new UsuarioMusica();
                usuarioMusica.setMusica(musicaEntity);
                usuarioMusica.setUsuario(usuario);
                usuarioMusica.setSelecionada(true);
                usuarioMusica.setFonte(FonteMusica.SPOTIFY);
                usuarioMusicaRepository.save(usuarioMusica);


            } catch (IOException  e) {
                System.err.println("Erro processando música " + topMusicaDto.getTitulo() + ": " + e.getMessage());
            }
        }
    }











}
