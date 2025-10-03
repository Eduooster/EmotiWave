package org.example.emotiwave.application.service;

import org.example.emotiwave.domain.entities.SpotifyToken;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.infra.repository.SpotifyTokenRepository;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Service
public class UsuarioSpotifyTokenService {

    @Autowired UsuarioRepository usuarioRepository;

    @Autowired
    SpotifyTokenRepository spotifyTokenRepository;

    public void vincularToken(Map<String,Object> tokens, Usuario usuario) {

        System.out.println("vinculo token" + tokens);

        int expiresIn = ((Number) tokens.get("expires_in")).intValue();

        Instant expirationTime = Instant.now().plusSeconds(expiresIn);


        SpotifyToken spotifyToken = new SpotifyToken();
        spotifyToken.setAcess_token(tokens.get("access_token").toString());
        spotifyToken.setRefresh_token(tokens.get("refresh_token").toString());
        spotifyToken.setExpires_in(expirationTime);
        spotifyTokenRepository.save(spotifyToken);

        usuario.setSpotify_info(spotifyToken);
        spotifyToken.setUsuario(usuario);

        usuarioRepository.save(usuario);

    }
}
