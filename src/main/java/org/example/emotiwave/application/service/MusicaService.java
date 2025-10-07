package org.example.emotiwave.application.service;

import org.example.emotiwave.application.dto.in.MusicasCurtidasRequest;
import org.example.emotiwave.application.dto.out.MusicasMaisOuvidasResponseDto;
import org.example.emotiwave.application.mapper.MusicaMapper;
import org.example.emotiwave.domain.entities.FonteMusica;
import org.example.emotiwave.domain.entities.Musica;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.example.emotiwave.infra.client.SpotifyClient;
import org.example.emotiwave.infra.repository.MusicaRepository;
import org.example.emotiwave.infra.repository.UsuarioMusicaRepository;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MusicaService {

    private final UsuarioSpotifyTokenService usuarioSpotifyTokenService;
    private final UsuarioRepository usuarioRepository;
    private final SpotifyClient spotifyAuthService;
    private final MusicaRepository musicaRepository;
    private final MusicaMapper musicaMapper;
    private final UsuarioMusicaRepository usuarioMusicaRepository;


    public void  MusicaService(String authHeader) {



    }


    private MusicaService(UsuarioSpotifyTokenService usuarioSpotifyTokenService, UsuarioRepository usuarioRepository, SpotifyClient spotifyAuthService, MusicaRepository musicaRepository, MusicaMapper musicaMapper, UsuarioMusicaRepository usuarioMusicaRepository) {

        this.usuarioSpotifyTokenService = usuarioSpotifyTokenService;
        this.usuarioRepository = usuarioRepository;
        this.spotifyAuthService = spotifyAuthService;
        this.musicaRepository = musicaRepository;
        this.musicaMapper = musicaMapper;
        this.usuarioMusicaRepository = usuarioMusicaRepository;
    }


    public Page<MusicasMaisOuvidasResponseDto> listarMusicasMaisOuvidas(Pageable paginacao) {
        return musicaRepository.findAll(paginacao).map(musicaMapper::toMusicaMaisOuvidasResponseDto);
    }

    public void adionarMusicasCurtidas(MusicasCurtidasRequest musicasCurtidasRequest) {

        Usuario usuario = usuarioRepository.findById(musicasCurtidasRequest.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Set<Long> selecionadasSet = new HashSet<>(musicasCurtidasRequest.musicasCurtidas());

        List<UsuarioMusica> relacoes = usuarioMusicaRepository.findByUsuario(usuario);

        if (relacoes.isEmpty()) {
            List<Musica> musicasSelecionadas = musicaRepository.findAllById(selecionadasSet);

            List<UsuarioMusica> novasRelacoes = musicasSelecionadas.stream()
                    .map(musica -> {
                        UsuarioMusica um = new UsuarioMusica();
                        um.setUsuario(usuario);
                        um.setMusica(musica);
                        um.setSelecionada(true);
                        um.setFonte(FonteMusica.MANUAL);
                        return um;
                    })
                    .toList();
            System.out.println(novasRelacoes);

            usuarioMusicaRepository.saveAll(novasRelacoes);
        } else {

            for (UsuarioMusica relacao : relacoes) {
                boolean deveSerSelecionada = selecionadasSet.contains(relacao.getMusica().getId());
                relacao.setSelecionada(deveSerSelecionada);
            }
            usuarioMusicaRepository.saveAll(relacoes);
        }


    }
}

