package org.example.emotiwave.application.mapper;

import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMusicaMapper {
    UsuarioMusica toEntity(UsuarioMusica usuarioMusica);
}
