package org.example.emotiwave.application.dto.out;

import jakarta.annotation.sql.DataSourceDefinition;
import lombok.Data;

import java.util.List;

@Data
public class MusicasDoDiaSpotifyDto {
    private List<Item> items;

    @Data
    public static class Item {
        private Track track;
        private String played_at; // ou OffsetDateTime
        // getters e setters
    }
    @Data
    public static class Track {
        private String id;
        private String name;
        private List<Artist> artists;
        // getters e setters
    }

    @Data
    public static class Artist {
        private String name;
        private String id;
        // getters e setters
    }
}
