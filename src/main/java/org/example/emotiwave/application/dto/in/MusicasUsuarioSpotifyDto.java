package org.example.emotiwave.application.dto.in;

import java.util.List;

import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MusicasUsuarioSpotifyDto {
    private List<Track> items;

    @Data
    public static class Track {
        private String name;
        private List<Artist> artists;
        private String id;
        private String genero;


        public String getArtistsNames() {
            if (artists == null || artists.isEmpty()) return "Desconhecido";
            return artists.stream()
                    .map(Artist::getName)
                    .collect(Collectors.joining(", "));
        }

        public String getArtistsIds() {
            if (artists == null || artists.isEmpty()) return "";
            return artists.stream()
                    .map(Artist::getId)
                    .collect(Collectors.joining(","));
        }

        @Data
        public static class Artist {
            private String name;
            private String id;
        }

    }
}

