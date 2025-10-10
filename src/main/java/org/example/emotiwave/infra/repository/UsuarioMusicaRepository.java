package org.example.emotiwave.infra.repository;

import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.domain.entities.Musica;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsuarioMusicaRepository  extends JpaRepository<UsuarioMusica, Long> {
    List<UsuarioMusica> findByUsuario(Usuario usuario);



    UsuarioMusica findByUsuarioId(Long id);
    @Query(value = """
    SELECT 
       new org.example.emotiwave.application.dto.in.MusicaSimplesDto(
            m.titulo,
            m.artista,
            m.spotifyTrackId
           
        )
    FROM UsuarioMusica um
    JOIN um.musica m
    GROUP BY 
        m.titulo, m.artista, m.spotifyTrackId
    ORDER BY 
        COUNT(um.usuario.id) DESC
""")
    List<MusicaSimplesDto> findMusicasMaisOuvidas(Pageable pageable);


    UsuarioMusica findByMusica_SpotifyTrackIdAndUsuarioId(String spotifyTrackId,Long usuarioId);
}
