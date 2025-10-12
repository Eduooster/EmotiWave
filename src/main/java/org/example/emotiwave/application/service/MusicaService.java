package org.example.emotiwave.application.service;

import org.example.emotiwave.application.dto.in.BuscarMusicaDto;

import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.out.MusicasMaisOuvidasResponseDto;
import org.example.emotiwave.application.mapper.MusicaMapper;
import org.example.emotiwave.application.service.spotifyServices.SpotifyService;

import org.example.emotiwave.domain.entities.Musica;

import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.example.emotiwave.domain.exceptions.MusicaNaoEcontrada;
import org.example.emotiwave.domain.exceptions.MusicaSpotifyNaoEncontrada;

import org.example.emotiwave.infra.repository.MusicaRepository;
import org.example.emotiwave.infra.repository.UsuarioMusicaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MusicaService {




    private final MusicaRepository musicaRepository;
    private final MusicaMapper musicaMapper;
    private final UsuarioMusicaRepository usuarioMusicaRepository;
    private final SpotifyService spotifyService;

    String BUSCAR_MUSICA = "https://api.spotify.com/v1/search?q=";


    private MusicaService(MusicaRepository musicaRepository, MusicaMapper musicaMapper, UsuarioMusicaRepository usuarioMusicaRepository, SpotifyService spotifyService) {


        this.musicaRepository = musicaRepository;
        this.musicaMapper = musicaMapper;
        this.usuarioMusicaRepository = usuarioMusicaRepository;

        this.spotifyService = spotifyService;
    }


    public Page<MusicaSimplesDto> listarMusicasMaisOuvidas(Pageable paginacao) {

        return usuarioMusicaRepository.findMusicasMaisOuvidas(paginacao);
    }




    public Musica buscarMusica(String artista,String titulo) {
        Musica musicaRepo = musicaRepository.findByArtistaAndTitulo(artista, titulo);

        if (musicaRepo == null) {
            throw new MusicaSpotifyNaoEncontrada("Falha na busca de musica");
        }
        return musicaRepo;
    }


}

