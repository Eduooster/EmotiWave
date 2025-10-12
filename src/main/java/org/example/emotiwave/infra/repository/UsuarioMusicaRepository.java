package org.example.emotiwave.infra.repository;

import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.domain.entities.Musica;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UsuarioMusicaRepository  extends JpaRepository<UsuarioMusica, Long> {
    List<UsuarioMusica> findByUsuario(Usuario usuario);



    UsuarioMusica findByUsuarioId(Long id);

    @Query(value = """
    SELECT 
       new org.example.emotiwave.application.dto.in.MusicaSimplesDto(
            m.titulo,
            m.artista,
            m.spotifyTrackId,
            m.artistaId,
            m.genero
           
        )
    FROM UsuarioMusica um
    JOIN um.musica m
    GROUP BY 
        m.titulo, m.artista, m.spotifyTrackId,m.artistaId, m.genero
    ORDER BY 
        COUNT(um.usuario.id) DESC
""")
    Page<MusicaSimplesDto> findMusicasMaisOuvidas(Pageable pageable);

    @Query("""
        SELECT 
       new org.example.emotiwave.application.dto.in.MusicaSimplesDto(
            m.titulo,
            m.artista,
            m.spotifyTrackId,
            m.artistaId,
            m.genero
           
        )
    FROM UsuarioMusica um
    JOIN um.musica m
    WHERE um.usuario = :usuario and um.ouvidaEm = current_date  
    ORDER BY 
        um.ouvidaEm desc
        

    """)
    List<MusicaSimplesDto> findMusicasOuvidasHoje(@Param("usuario") Usuario usuario, Pageable pageable);

    List<UsuarioMusica> findByUsuarioAndOuvidaEmAndSelecionadaTrue(Usuario usuario, LocalDate ouvidaEm);





    UsuarioMusica findByMusica_SpotifyTrackIdAndUsuarioId(String spotifyTrackId,Long usuarioId);

    UsuarioMusica findByMusicaId(Integer musicaId);

    UsuarioMusica findByUsuarioIdAndMusicaSpotifyTrackId(Long usuarioId, String spotifyTrackId);
}
