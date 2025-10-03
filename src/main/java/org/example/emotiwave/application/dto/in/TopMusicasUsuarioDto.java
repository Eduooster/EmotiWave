package org.example.emotiwave.application.dto.in;

import java.util.List;

import lombok.Data;
import java.util.List;

@Data
public class TopMusicasUsuarioDto {
    private List<Track> items;

    @Data
    public static class Track {
        private String name; // nome da música
        private List<Artist> artists;
        private String id;

        @Data
        public static class Artist {
            private String name; // nome do artista
        }
    }
}

