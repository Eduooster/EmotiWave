package org.example.emotiwave.application.dto.out;

import jakarta.validation.constraints.NotBlank;
import org.example.emotiwave.domain.entities.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record UsuarioDetailResponseDto(Long id,
                                        @NotBlank String username,
                                        @NotBlank String email,
                                        @NotBlank LocalDate criadoEm,
                                        @NotBlank Set<Role> roles) {
}
