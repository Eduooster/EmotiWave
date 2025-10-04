package org.example.emotiwave.application.mapper;

import javax.annotation.processing.Generated;
import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.domain.entities.Musica;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-04T11:34:01-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class MusicaMapperImpl implements MusicaMapper {

    @Override
    public MusicaSimplesDto toEntity(Musica musica) {
        if ( musica == null ) {
            return null;
        }

        String titulo = null;
        String artista = null;

        titulo = musica.getTitulo();
        artista = musica.getArtista();

        String spotify_track_id = null;

        MusicaSimplesDto musicaSimplesDto = new MusicaSimplesDto( titulo, artista, spotify_track_id );

        return musicaSimplesDto;
    }
}
