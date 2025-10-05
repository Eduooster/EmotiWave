package org.example.emotiwave.application.mapper;

import javax.annotation.processing.Generated;
import org.example.emotiwave.application.dto.out.AcessTokenResponseDto;
import org.example.emotiwave.domain.entities.SpotifyToken;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-05T11:56:38-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class SpotifyTokenMapperImpl implements SpotifyTokenMapper {

    @Override
    public SpotifyToken toEntity(AcessTokenResponseDto dto) {
        if ( dto == null ) {
            return null;
        }

        SpotifyToken spotifyToken = new SpotifyToken();

        spotifyToken.setAccessToken( dto.accessToken() );
        spotifyToken.setRefreshToken( dto.refreshToken() );

        spotifyToken.setExpiresIn( java.time.Instant.now().plusSeconds(dto.expiresIn()) );

        return spotifyToken;
    }
}
