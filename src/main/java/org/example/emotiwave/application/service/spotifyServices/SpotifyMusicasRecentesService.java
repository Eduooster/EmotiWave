package org.example.emotiwave.application.service.spotifyServices;

import jakarta.transaction.Transactional;
import org.example.emotiwave.application.dto.in.GeneroMusicaSpotifyDto;
import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.out.MusicasDoDiaSpotifyDto;
import org.example.emotiwave.application.mapper.MusicaMapper;

import org.example.emotiwave.application.service.HuggingFaceZeroShotService;

import org.example.emotiwave.domain.entities.*;
import org.example.emotiwave.domain.exceptions.HuggingFaceException;
import org.example.emotiwave.domain.exceptions.LetraMusicaNaoEncontradaGenius;
import org.example.emotiwave.infra.client.GeniusLyricsClient;
import org.example.emotiwave.infra.client.HuggingFaceZeroShotClient;

import org.example.emotiwave.infra.repository.MusicaRepository;
import org.example.emotiwave.infra.repository.UsuarioMusicaRepository;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class SpotifyMusicasRecentesService {


    private final MusicaRepository musicaRepository;
    private final SpotifyService spotifyService;
    private final UsuarioMusicaRepository usuarioMusicaRepository;
    private final MusicaMapper musicaMapper;
    private final GeniusLyricsClient geniusLyricsClient;

    private final HuggingFaceZeroShotService huggingFaceZeroShotService;
    String SPOTIFY_URL_GENERO = "https://api.spotify.com/v1/artists/";
    String SPOTIFY_URL_RECENTLY_PLAYED = "https://api.spotify.com/v1/me/player/recently-played?limit=3";



    public SpotifyMusicasRecentesService( MusicaRepository musicaRepository, SpotifyService spotifyService, UsuarioMusicaRepository usuarioMusicaRepository, MusicaMapper musicaMapper, GeniusLyricsClient geniusLyricsClient, HuggingFaceZeroShotService huggingFaceZeroShotService) {

        this.musicaRepository = musicaRepository;
        this.spotifyService = spotifyService;
        this.usuarioMusicaRepository = usuarioMusicaRepository;
        this.musicaMapper = musicaMapper;
        this.geniusLyricsClient = geniusLyricsClient;
        this.huggingFaceZeroShotService = huggingFaceZeroShotService;

    }


    public ResponseEntity<List<MusicaSimplesDto>> buscarMusicasOuvidasRecentes(Usuario usuario) throws IOException, InterruptedException {
        spotifyService.verificarExpiracaoToken(usuario);

        Long timeStampDoDia = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        MusicasDoDiaSpotifyDto musicasUsuarioSpotifyDto = spotifyService.enviarRequisicaoSpotifyUtils(usuario,
                SPOTIFY_URL_RECENTLY_PLAYED,
                new ParameterizedTypeReference<>() {
                },
                timeStampDoDia);


        List<MusicaSimplesDto> musicaSimplesDto =converterMusicasDiaParaDto (musicasUsuarioSpotifyDto,usuario);

        associarMusicasAoUsuario(musicaSimplesDto,usuario);

        return ResponseEntity.ok(musicaSimplesDto);


    }

    private List<MusicaSimplesDto> converterMusicasDiaParaDto(MusicasDoDiaSpotifyDto dtoSpotify, Usuario usuario) {
        if (dtoSpotify == null || dtoSpotify.getItems() == null || dtoSpotify.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        return dtoSpotify.getItems().stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    if (item.getTrack() == null) return null;

                    GeneroMusicaSpotifyDto genero = spotifyService.getGeneros(item.getTrack().getArtists().getFirst().getId(),usuario, new ParameterizedTypeReference<>() {},null,SPOTIFY_URL_GENERO);


                    String nomeMusica = item.getTrack().getName() != null ? item.getTrack().getName() : "Título desconhecido";
                    String artista = (item.getTrack().getArtists() != null && !item.getTrack().getArtists().isEmpty())
                            ? item.getTrack().getArtists().getFirst().getName()
                            : "Artista desconhecido";
                    String artistaId = item.getTrack().getArtists().getFirst().getId();
                    String spotifyId = item.getTrack().getId();
                    String generoString = (genero != null && !genero.genres().isEmpty())
                            ? genero.genres().get(0)   // pega o primeiro gênero
                            : "Desconhecido";




                    return new MusicaSimplesDto(nomeMusica, artista, spotifyId,artistaId,generoString);
                })
                .filter(Objects::nonNull)
                .toList();
    }


    private Musica criarNovaMusicaComAnalise(MusicaSimplesDto musicaDto)
            throws IOException, InterruptedException {

        Musica musica = musicaMapper.toEntity(musicaDto);
        musicaRepository.save(musica);

        try {
            String letra = geniusLyricsClient.fetchLyrics(musica.getArtista(), musica.getTitulo());
            musica.setLetra(letra);
            AnaliseMusica analise = huggingFaceZeroShotService.analisarMusica(musica);
            musica.setAnalise(analise);
        } catch (LetraMusicaNaoEncontradaGenius e) {
            musica.setLetra("Letra não disponível");
            musica.setAnalise(null);
        }catch (HuggingFaceException e){
            musica.setAnalise(null);
        }








        return musica;
    }

    @Transactional
    protected void associarMusicasAoUsuario(List<MusicaSimplesDto> musicasSpotifyDto, Usuario usuario)
            throws IOException, InterruptedException {

        for (MusicaSimplesDto musicaDto : musicasSpotifyDto) {
            UsuarioMusica usuarioMusica = usuarioMusicaRepository
                    .findByMusica_SpotifyTrackIdAndUsuarioId(musicaDto.getSpotifyTrackId(), usuario.getId());

            if (usuarioMusica != null) {
                usuarioMusica.setOuvidaEm(LocalDate.now());
                usuarioMusicaRepository.save(usuarioMusica);
                continue;
            }

            Musica musica = musicaRepository.findBySpotifyTrackId(musicaDto.getSpotifyTrackId());

            if (musica == null) {
                musica = criarNovaMusicaComAnalise(musicaDto);
                musica = musicaRepository.save(musica);
            }



            UsuarioMusica novaAssociacao = new UsuarioMusica();
            novaAssociacao.setUsuario(usuario);
            novaAssociacao.setMusica(musica);
            novaAssociacao.setOuvidaEm(LocalDate.now());
            novaAssociacao.setSelecionada(true);
            novaAssociacao.setFonte(FonteMusica.SPOTIFY);

            usuarioMusicaRepository.save(novaAssociacao);
        }
    }


}
