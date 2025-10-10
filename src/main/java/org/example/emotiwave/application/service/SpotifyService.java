package org.example.emotiwave.application.service;

import org.example.emotiwave.application.dto.in.MusicasUsuarioSpotifyDto;
import org.example.emotiwave.application.dto.out.AcessTokenResponseDto;
import org.example.emotiwave.application.dto.out.MusicasDoDiaUsuarioSpotifyDto;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.infra.client.SpotifyClient;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SpotifyService {
    private final SpotifyClient spotifyClient;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioSpotifyTokenService usuarioSpotifyTokenService;
    Instant now = Instant.now();

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

    public void verificarExpiracaoToken(Usuario usuario) {


        if (usuario.getSpotify_info().getExpiresIn().isBefore(now.plusSeconds(5 * 60))) {
            AcessTokenResponseDto newAccessToken = spotifyClient.refreshAccessToken(usuario);
            System.out.println(newAccessToken);
            usuario.getSpotify_info().setAccessToken(newAccessToken.accessToken());
            usuario.getSpotify_info().setExpiresIn(Instant.ofEpochSecond(newAccessToken.expiresIn()));
            usuarioRepository.save(usuario);
        }
    }

    //ARRUMAR ISSO
    public MusicasDoDiaUsuarioSpotifyDto enviarRequisicaoSpotifyUtils(Usuario usuario, String url, ParameterizedTypeReference<MusicasDoDiaUsuarioSpotifyDto> parameterizedTypeReference, Long after ) {
        return spotifyClient.enviarRequisicaoSpotifyUtils(usuario,url,parameterizedTypeReference,after);

    }
    //ARRUMAR ISSO
    public MusicasUsuarioSpotifyDto enviarRequisicaoSpotifyUtilsV2(Usuario usuario, String url, ParameterizedTypeReference<MusicasUsuarioSpotifyDto> parameterizedTypeReference, Long after ) {
        return spotifyClient.enviarRequisicaoSpotifyUtils(usuario,url,parameterizedTypeReference,after);

    }
}
