package org.example.emotiwave.application.mapper;

import javax.annotation.processing.Generated;
import org.example.emotiwave.application.dto.out.AccessTokenResponseDto;
import org.example.emotiwave.domain.entities.SpotifyToken;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-12T18:26:39-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class SpotifyTokenMapperImpl implements SpotifyTokenMapper {

    @Override
    public SpotifyToken toEntity(AccessTokenResponseDto dto) {
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
