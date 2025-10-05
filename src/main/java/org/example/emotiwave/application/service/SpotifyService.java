package org.example.emotiwave.application.service;

import org.example.emotiwave.application.dto.out.AcessTokenResponseDto;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.infra.client.SpotifyClient;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class SpotifyService {
    private final SpotifyClient spotifyClient;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioSpotifyTokenService usuarioSpotifyTokenService;

    public SpotifyService(SpotifyClient spotifyClient, UsuarioRepository usuarioRepository, UsuarioSpotifyTokenService usuarioSpotifyTokenService) {
        this.spotifyClient = spotifyClient;
        this.usuarioRepository = usuarioRepository;
        this.usuarioSpotifyTokenService = usuarioSpotifyTokenService;
    }

    public String solicitarAutorizacao(String authHeader){
        return  spotifyClient.contruirAutorizacao(authHeader);
    }

    public void exchangeCodeForTokens(String code, Usuario usuario) {

        AcessTokenResponseDto acessTokenResponse = spotifyClient.exchangeCodeForTokens(code,usuario);
        usuarioSpotifyTokenService.vincularToken(acessTokenResponse, usuario);

    }








}
