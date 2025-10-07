package org.example.emotiwave.application.dto.out;

import java.util.List;

public record MusicasMaisOuvidasResponseDto(List<MusicaResponseDto> musicas) {

    public record MusicaResponseDto(Long id,
                                    String artista,
                                    String titulo,
                                    Integer qtdOuvintes){

    }
}


