package org.example.emotiwave.application.service;

import org.example.emotiwave.application.dto.out.AcessTokenResponseDto;
import org.example.emotiwave.application.mapper.SpotifyTokenMapper;
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

    @Autowired
    SpotifyTokenMapper spotifyTokenMapper;

    public void vincularToken(AcessTokenResponseDto tokensDto, Usuario usuario) {

        SpotifyToken spotifyToken = spotifyTokenMapper.toEntity(tokensDto);
        spotifyTokenRepository.save(spotifyToken);

        usuario.setSpotify_info(spotifyToken);
        spotifyToken.setUsuario(usuario);

        usuarioRepository.save(usuario);
    }
}
