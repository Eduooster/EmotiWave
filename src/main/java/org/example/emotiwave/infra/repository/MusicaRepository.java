package org.example.emotiwave.infra.repository;

import org.example.emotiwave.domain.entities.Musica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicaRepository extends JpaRepository<Musica, Long> {


    Musica findBySpotifyTrackId(String spotifyTrackId);

    Musica findByArtistaAndTitulo(String artista, String titulo);
}
