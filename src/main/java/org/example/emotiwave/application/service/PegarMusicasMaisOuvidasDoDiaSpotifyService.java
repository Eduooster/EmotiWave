package org.example.emotiwave.application.service;

import jakarta.transaction.Transactional;
import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.in.MusicasUsuarioSpotifyDto;
import org.example.emotiwave.application.dto.out.MusicasDoDiaUsuarioSpotifyDto;
import org.example.emotiwave.application.mapper.MusicaMapper;
import org.example.emotiwave.application.mapper.UsuarioMusicaMapper;
import org.example.emotiwave.domain.entities.*;
import org.example.emotiwave.domain.exceptions.LetraMusicaNaoEncontradaGenius;
import org.example.emotiwave.infra.client.GeniusLyricsClient;
import org.example.emotiwave.infra.client.HuggingFaceZeroShotClient;
import org.example.emotiwave.infra.client.SpotifyClient;
import org.example.emotiwave.infra.repository.MusicaRepository;
import org.example.emotiwave.infra.repository.UsuarioMusicaRepository;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class PegarMusicasMaisOuvidasDoDiaSpotifyService {

    private final SpotifyClient spotifyClient;
    private final MusicaRepository musicaRepository;
    private final SpotifyService spotifyService;
    private final UsuarioMusicaRepository usuarioMusicaRepository;
    private final UsuarioMusicaMapper usuarioMusicaMapper;
    private final UsuarioRepository usuarioRepository;
    private final MusicaService musicaService;
    private final MusicaMapper musicaMapper;
    private final GeniusLyricsClient geniusLyricsClient;
    private final HuggingFaceZeroShotClient huggingFaceZeroShotClient;
    String url = "https://api.spotify.com/v1/me/player/recently-played";



    public PegarMusicasMaisOuvidasDoDiaSpotifyService(SpotifyClient spotifyClient, MusicaRepository musicaRepository, SpotifyService spotifyService, UsuarioMusicaRepository usuarioMusicaRepository, UsuarioMusicaMapper usuarioMusicaMapper, UsuarioRepository usuarioRepository, MusicaService musicaService, MusicaMapper musicaMapper, GeniusLyricsClient geniusLyricsClient, HuggingFaceZeroShotClient huggingFaceZeroShotClient) {
        this.spotifyClient = spotifyClient;
        this.musicaRepository = musicaRepository;
        this.spotifyService = spotifyService;
        this.usuarioMusicaRepository = usuarioMusicaRepository;
        this.usuarioMusicaMapper = usuarioMusicaMapper;
        this.usuarioRepository = usuarioRepository;
        this.musicaService = musicaService;
        this.musicaMapper = musicaMapper;
        this.geniusLyricsClient = geniusLyricsClient;
        this.huggingFaceZeroShotClient = huggingFaceZeroShotClient;
    }

    public ResponseEntity<List<MusicaSimplesDto>> pegarMusicasMaisOuvidasDoDia(Usuario usuario) throws IOException, InterruptedException {
        spotifyService.verificarExpiracaoToken(usuario);

        Long timeStampDoDia = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        MusicasDoDiaUsuarioSpotifyDto musicasUsuarioSpotifyDto = spotifyService.enviarRequisicaoSpotifyUtils(usuario,
                url,
                new ParameterizedTypeReference<MusicasDoDiaUsuarioSpotifyDto>() {},
                timeStampDoDia);




        List<MusicaSimplesDto> musicaSimplesDto =musicasDiaResponseToMusicaSimplesDto (musicasUsuarioSpotifyDto);

        associarMusicasAoUsuario(musicaSimplesDto,usuario);

        return ResponseEntity.ok(musicaSimplesDto);


    }

    private List<MusicaSimplesDto> musicasDiaResponseToMusicaSimplesDto(MusicasDoDiaUsuarioSpotifyDto dtoSpotify) {
        if (dtoSpotify == null || dtoSpotify.getItems() == null || dtoSpotify.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        return dtoSpotify.getItems().stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    if (item.getTrack() == null) return null;

                    String nomeMusica = item.getTrack().getName() != null ? item.getTrack().getName() : "Título desconhecido";
                    String artista = (item.getTrack().getArtists() != null && !item.getTrack().getArtists().isEmpty())
                            ? item.getTrack().getArtists().get(0).getName()
                            : "Artista desconhecido";
                    String spotifyId = item.getTrack().getId();

                    return new MusicaSimplesDto(nomeMusica, artista, spotifyId);
                })
                .filter(Objects::nonNull)
                .toList();
    }


    private Musica criarNovaMusicaComAnalise(MusicaSimplesDto musicaDto)
            throws IOException, InterruptedException {
        System.out.println("Musica nova sendo criada" + musicaDto.getTitulo());
        Musica musica = musicaMapper.toEntity(musicaDto);
        musicaRepository.save(musica);

        try {
            String letra = geniusLyricsClient.fetchLyrics(musica.getArtista(), musica.getTitulo());
            musica.setLetra(letra);
            AnaliseMusica analise = huggingFaceZeroShotClient.analisarScoreMusica(musica);
            musica.setAnalise(analise);
        } catch (LetraMusicaNaoEncontradaGenius e) {
            musica.setLetra("Letra não disponível");
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

            System.out.println("Musica ID antes de associar: " + musica.getId()); // debug

            UsuarioMusica novaAssociacao = new UsuarioMusica();
            novaAssociacao.setUsuario(usuario);
            novaAssociacao.setMusica(musica);
            novaAssociacao.setOuvidaEm(LocalDate.now());
            novaAssociacao.setFonte(FonteMusica.SPOTIFY);

            usuarioMusicaRepository.save(novaAssociacao);
        }
    }


}
