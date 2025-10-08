package org.example.emotiwave.infra.repository;

import org.example.emotiwave.domain.entities.Musica;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioMusicaRepository  extends JpaRepository<UsuarioMusica, Long> {
    List<UsuarioMusica> findByUsuario(Usuario usuario);



    UsuarioMusica findByUsuarioId(Long id);




    UsuarioMusica findByMusica_SpotifyTrackIdAndUsuarioId(String spotifyTrackId,Long usuarioId);
}
