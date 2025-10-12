package org.example.emotiwave.application.mapper;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.example.emotiwave.application.dto.in.UsuarioCreateRequestDto;
import org.example.emotiwave.application.dto.out.UsuarioDetailResponseDto;
import org.example.emotiwave.application.dto.out.UsuarioListResponseDto;
import org.example.emotiwave.domain.entities.Role;
import org.example.emotiwave.domain.entities.Usuario;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-12T18:26:39-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public Usuario toUsuario(UsuarioCreateRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setUsername( dto.username() );
        usuario.setPassword( dto.password() );
        usuario.setEmail( dto.email() );

        return usuario;
    }

    @Override
    public UsuarioDetailResponseDto toUsuarioDetailsReponseDto(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        Long id = null;
        String username = null;
        String email = null;
        LocalDate criadoEm = null;
        Set<Role> roles = null;

        id = usuario.getId();
        username = usuario.getUsername();
        email = usuario.getEmail();
        criadoEm = usuario.getCriadoEm();
        Set<Role> set = usuario.getRoles();
        if ( set != null ) {
            roles = new LinkedHashSet<Role>( set );
        }

        UsuarioDetailResponseDto usuarioDetailResponseDto = new UsuarioDetailResponseDto( id, username, email, criadoEm, roles );

        return usuarioDetailResponseDto;
    }

    @Override
    public UsuarioListResponseDto toListaResponseDto(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        String email = null;
        String username = null;

        email = usuario.getEmail();
        username = usuario.getUsername();

        UsuarioListResponseDto usuarioListResponseDto = new UsuarioListResponseDto( email, username );

        return usuarioListResponseDto;
    }
}
