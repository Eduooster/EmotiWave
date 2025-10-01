package org.example.emotiwave.infra.repository;

import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.repository.UsuarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositoryImpl extends UsuarioRepository, JpaRepository<Usuario, Long> {

}
