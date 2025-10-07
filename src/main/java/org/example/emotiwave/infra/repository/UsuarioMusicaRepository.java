package org.example.emotiwave.infra.repository;

import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsuarioMusicaRepository  extends JpaRepository<UsuarioMusica, Long> {
    List<UsuarioMusica> findByUsuario(Usuario usuario);
}
