package org.example.emotiwave.application.mapper;

import org.example.emotiwave.application.dto.out.AcessTokenResponseDto;
import org.example.emotiwave.domain.entities.SpotifyToken;
import org.mapstruct.Mapper;
import java.time.Instant;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpotifyTokenMapper {

    @Mapping(target = "expiresIn", expression = "java(java.time.Instant.now().plusSeconds(dto.expiresIn()))")
    SpotifyToken toEntity(AcessTokenResponseDto dto);
}
