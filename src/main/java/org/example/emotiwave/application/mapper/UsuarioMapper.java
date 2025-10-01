package org.example.emotiwave.application.mapper;

import org.example.emotiwave.application.dto.in.UsuarioCreateRequestDto;
import org.example.emotiwave.application.dto.out.UsuarioDetailsResponseDto;
import org.example.emotiwave.domain.entities.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toUsuario(UsuarioCreateRequestDto dto);

    UsuarioDetailsResponseDto toUsuarioDetailsReponseDto(Usuario usuario);
}
