package org.example.emotiwave.application.mapper;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.example.emotiwave.application.dto.in.UsuarioCreateRequestDto;
import org.example.emotiwave.application.dto.out.UsuarioDetailsResponseDto;
import org.example.emotiwave.domain.entities.Role;
import org.example.emotiwave.domain.entities.Usuario;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-01T13:38:58-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
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
    public UsuarioDetailsResponseDto toUsuarioDetailsReponseDto(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        Long id = null;
        String username = null;
        String email = null;
        Set<Role> roles = null;

        id = usuario.getId();
        username = usuario.getUsername();
        email = usuario.getEmail();
        Set<Role> set = usuario.getRoles();
        if ( set != null ) {
            roles = new LinkedHashSet<Role>( set );
        }

        LocalDateTime dataCadastro = null;

        UsuarioDetailsResponseDto usuarioDetailsResponseDto = new UsuarioDetailsResponseDto( id, username, email, dataCadastro, roles );

        return usuarioDetailsResponseDto;
    }
}
