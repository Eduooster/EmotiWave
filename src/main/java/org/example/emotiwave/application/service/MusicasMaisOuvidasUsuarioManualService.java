package org.example.emotiwave.application.service;

import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.service.UsuarioMusicaServices.UsuarioMusicaService;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.example.emotiwave.infra.repository.UsuarioMusicaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicasMaisOuvidasUsuarioManualService {

    private final UsuarioMusicaRepository usuarioMusicaRepository;
    private final UsuarioMusicaService usuarioMusicaService;


    public MusicasMaisOuvidasUsuarioManualService(UsuarioMusicaRepository usuarioMusicaRepository, UsuarioMusicaService usuarioMusicaService) {

        this.usuarioMusicaRepository = usuarioMusicaRepository;
        this.usuarioMusicaService = usuarioMusicaService;
    }

    public List<MusicaSimplesDto> musicasRecemOuvidas(Pageable paginacao, Usuario usuario) {


        return usuarioMusicaRepository.findByUsuarioAndOuvidaEmAndSelecionadaTrue(usuario, LocalDate.now())
                .stream()
                .map(um -> new MusicaSimplesDto(
                        um.getMusica().getTitulo(),
                        um.getMusica().getArtista(),
                        um.getMusica().getSpotifyTrackId(),
                        um.getMusica().getArtistaId(),
                        um.getMusica().getGenero()
                ))
                .collect(Collectors.toList());





    }
}
