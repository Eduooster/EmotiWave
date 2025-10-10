package org.example.emotiwave.application.dto.in;

import lombok.Data;

import java.util.List;


@Data
public class MusicasSelecionadasDto {
    private List<Item> items;

    @Data
    public static class Item {
        private String spotifyTrackId;
        private boolean ouvidaHoje;

    }

}
