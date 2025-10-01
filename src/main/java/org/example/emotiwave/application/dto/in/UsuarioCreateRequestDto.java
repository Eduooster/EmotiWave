package org.example.emotiwave.application.dto.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioCreateRequestDto(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank @Email String email
) {
}
