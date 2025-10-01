package org.example.emotiwave.application.service;

import jakarta.validation.Valid;
import org.example.emotiwave.application.dto.in.UsuarioCreateRequestDto;
import org.example.emotiwave.application.dto.out.UsuarioDetailsResponseDto;
import org.example.emotiwave.application.mapper.UsuarioMapper;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.exceptions.UsuarioJaCadastrado;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CrudUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;


    public CrudUsuarioService(UsuarioMapper usuarioMapper,UsuarioRepository usuarioRepository,  PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioDetailsResponseDto cadastrar(UsuarioCreateRequestDto dto) {

        Usuario usuario = usuarioMapper.toUsuario(dto);

        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new UsuarioJaCadastrado("Email em uso!"); });


        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);

        return usuarioMapper.toUsuarioDetailsReponseDto(usuario);

    }
}