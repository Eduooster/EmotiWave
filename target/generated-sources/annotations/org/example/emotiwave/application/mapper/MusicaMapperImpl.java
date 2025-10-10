package org.example.emotiwave.application.mapper;

import java.util.List;
import javax.annotation.processing.Generated;
import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.out.MusicasMaisOuvidasResponseDto;
import org.example.emotiwave.domain.entities.Musica;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-10T12:10:35-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
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
        musica.setArtistaId( musicaSimplesDto.getArtistaId() );

        return musica;
    }

    @Override
    public MusicasMaisOuvidasResponseDto toMusicaMaisOuvidasResponseDto(Musica musica) {
        if ( musica == null ) {
            return null;
        }

        List<MusicasMaisOuvidasResponseDto.MusicaResponseDto> musicas = null;

        MusicasMaisOuvidasResponseDto musicasMaisOuvidasResponseDto = new MusicasMaisOuvidasResponseDto( musicas );

        return musicasMaisOuvidasResponseDto;
    }
}
