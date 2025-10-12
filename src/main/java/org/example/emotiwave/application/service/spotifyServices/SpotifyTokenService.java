package org.example.emotiwave.application.service.spotifyServices;

import org.example.emotiwave.application.dto.out.AccessTokenResponseDto;
import org.example.emotiwave.application.mapper.SpotifyTokenMapper;
import org.example.emotiwave.domain.entities.SpotifyToken;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.infra.repository.SpotifyTokenRepository;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpotifyTokenService {

    @Autowired UsuarioRepository usuarioRepository;

    @Autowired
    SpotifyTokenRepository spotifyTokenRepository;

    @Autowired
    SpotifyTokenMapper spotifyTokenMapper;

    public void associarTokenAoUsuario(AccessTokenResponseDto tokensDto, Usuario usuario) {

        SpotifyToken spotifyToken = spotifyTokenMapper.toEntity(tokensDto);
        spotifyTokenRepository.save(spotifyToken);

        usuario.setSpotifyInfo(spotifyToken);
        spotifyToken.setUsuario(usuario);

        usuarioRepository.save(usuario);
    }
}
