package org.example.emotiwave.application.service;

import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.exceptions.GeniusLyricsNaoEncontrada;
import org.example.emotiwave.infra.client.GeniusLyricsClient;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service

public class GeniusLyricsService {

    private final GeniusLyricsClient geniusLyricsClient;

    public GeniusLyricsService(GeniusLyricsClient geniusLyricsClient) {
        this.geniusLyricsClient = geniusLyricsClient;
    }

    public String buscarLyrics(String artist, String title) {
        try {
            String letra = geniusLyricsClient.fetchLyrics(artist, title);
            if (letra == null || letra.isBlank()) {
                return "Letra não disponível";
            }
            return letra.trim();
        } catch ( IOException e) {
            throw new GeniusLyricsNaoEncontrada(e.getMessage());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
