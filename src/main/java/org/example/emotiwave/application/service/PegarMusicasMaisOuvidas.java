package org.example.emotiwave.application.service;

import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.infra.repository.UsuarioMusicaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PegarMusicasMaisOuvidas {

    private final UsuarioMusicaRepository usuarioMusicaRepository;


    public PegarMusicasMaisOuvidas(UsuarioMusicaRepository usuarioMusicaRepository) {

        this.usuarioMusicaRepository = usuarioMusicaRepository;
    }

    public List<MusicaSimplesDto> pegarMusicasMaisOuvidas(Pageable paginacao) {
        return usuarioMusicaRepository.findMusicasMaisOuvidas(paginacao);

    }
}
