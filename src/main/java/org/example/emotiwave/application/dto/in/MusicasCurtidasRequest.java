package org.example.emotiwave.application.dto.in;

import org.example.emotiwave.domain.entities.Usuario;

import java.util.List;
import java.util.Set;

public record MusicasCurtidasRequest(Long  usuarioId, Set<Long> musicasCurtidas) {
}
