package org.example.emotiwave.infra.repository;

import org.example.emotiwave.domain.entities.SpotifyToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyTokenRepository extends JpaRepository<SpotifyToken, Long> {
}
