package org.example.emotiwave.application.service;

import org.example.emotiwave.application.dto.in.UsuarioCreateRequestDto;

import org.example.emotiwave.application.dto.out.UsuarioDetailResponseDto;

import org.example.emotiwave.application.dto.out.UsuarioListResponseDto;
import org.example.emotiwave.application.mapper.UsuarioMapper;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.exceptions.UsuarioJaCadastrado;
import org.example.emotiwave.domain.exceptions.UsuarioNaoEncontrado;
import org.example.emotiwave.infra.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;


    public UsuarioService(UsuarioMapper usuarioMapper, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioDetailResponseDto cadastrar(UsuarioCreateRequestDto dto) {

        Usuario usuario = usuarioMapper.toUsuario(dto);

        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(u -> { throw new UsuarioJaCadastrado("Email em uso!"); });


        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);

        return usuarioMapper.toUsuarioDetailsReponseDto(usuario);

    }

    public Page<UsuarioDetailResponseDto> listar(Pageable pageable) {
        return usuarioRepository
                .findByDeletedFalse(pageable)
                .map(usuarioMapper::toUsuarioDetailsReponseDto);
    }

    public UsuarioDetailResponseDto excluir(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontrado("Id usuário inexistente"));

        usuario.setDeleted(true);

        usuarioRepository.save(usuario);


        return usuarioMapper.toUsuarioDetailsReponseDto(usuario);

    }

    public UsuarioDetailResponseDto buscarPorId(Long id) {
        Usuario usuario = usuarioRepository
                .findById(id).orElseThrow(()-> new UsuarioNaoEncontrado("Usuario não encontrado!"));

        return usuarioMapper.toUsuarioDetailsReponseDto(usuario);
    }


}