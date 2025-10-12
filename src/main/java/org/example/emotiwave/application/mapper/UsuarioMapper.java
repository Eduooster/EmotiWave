package org.example.emotiwave.application.mapper;

import org.example.emotiwave.application.dto.in.UsuarioCreateRequestDto;

import org.example.emotiwave.application.dto.out.UsuarioDetailResponseDto;

import org.example.emotiwave.application.dto.out.UsuarioListResponseDto;
import org.example.emotiwave.domain.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toUsuario(UsuarioCreateRequestDto dto);


    UsuarioDetailResponseDto toUsuarioDetailsReponseDto(Usuario usuario);

    UsuarioListResponseDto toListaResponseDto(Usuario usuario);
}
