package org.example.emotiwave.application.mapper;

import javax.annotation.processing.Generated;
import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.domain.entities.Musica;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-05T19:28:39-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class MusicaMapperImpl implements MusicaMapper {

    @Override
    public Musica toEntity(MusicaSimplesDto musicaSimplesDto) {
        if ( musicaSimplesDto == null ) {
            return null;
        }

        Musica musica = new Musica();

        musica.setTitulo( musicaSimplesDto.getTitulo() );
        musica.setArtista( musicaSimplesDto.getArtista() );
        musica.setSpotifyTrackId( musicaSimplesDto.getSpotifyTrackId() );

        return musica;
    }
}
