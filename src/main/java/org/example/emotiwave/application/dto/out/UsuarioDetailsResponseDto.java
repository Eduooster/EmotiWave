package org.example.emotiwave.application.dto.out;

import jakarta.validation.constraints.NotBlank;
import org.example.emotiwave.domain.entities.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record UsuarioDetailsResponseDto(Long id,
                                        @NotBlank String username,
                                        @NotBlank String email,
                                        @NotBlank LocalDateTime dataCadastro,
                                        @NotBlank Set<Role> roles) {
}
