package org.example.emotiwave.application.mapper;

import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.out.MusicasMaisOuvidasResponseDto;
import org.example.emotiwave.domain.entities.Musica;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MusicaMapper {

    Musica toEntity(MusicaSimplesDto musicaSimplesDto);

    MusicasMaisOuvidasResponseDto toMusicaMaisOuvidasResponseDto(Musica musica);




}
