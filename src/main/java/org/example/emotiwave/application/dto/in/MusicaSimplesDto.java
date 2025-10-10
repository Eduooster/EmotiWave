package org.example.emotiwave.application.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MusicaSimplesDto {
    private String titulo;
    private String artista;
    private String spotifyTrackId;
    private String artistaId;
}
