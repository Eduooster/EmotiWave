package org.example.emotiwave.application.service;

import org.example.emotiwave.infra.client.SpotifyClient;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class MusicaService {

    private final UsuarioSpotifyTokenService usuarioSpotifyTokenService;
    private final UsuarioRepository usuarioRepository;
    private final SpotifyClient spotifyAuthService;


    public void  MusicaService(String authHeader) {



    }


    private MusicaService(UsuarioSpotifyTokenService usuarioSpotifyTokenService, UsuarioRepository usuarioRepository, SpotifyClient spotifyAuthService) {

        this.usuarioSpotifyTokenService = usuarioSpotifyTokenService;
        this.usuarioRepository = usuarioRepository;
        this.spotifyAuthService = spotifyAuthService;
    }


}

