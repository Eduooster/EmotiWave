package org.example.emotiwave.application.mapper;

import javax.annotation.processing.Generated;
import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-10T12:10:35-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class UsuarioMusicaMapperImpl implements UsuarioMusicaMapper {

    @Override
    public UsuarioMusica toEntity(UsuarioMusica usuarioMusica) {
        if ( usuarioMusica == null ) {
            return null;
        }

        UsuarioMusica usuarioMusica1 = new UsuarioMusica();

        usuarioMusica1.setId( usuarioMusica.getId() );
        usuarioMusica1.setUsuario( usuarioMusica.getUsuario() );
        usuarioMusica1.setMusica( usuarioMusica.getMusica() );
        usuarioMusica1.setSelecionada( usuarioMusica.isSelecionada() );
        usuarioMusica1.setOuvidaEm( usuarioMusica.getOuvidaEm() );
        usuarioMusica1.setFonte( usuarioMusica.getFonte() );

        return usuarioMusica1;
    }
}
