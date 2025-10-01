package org.example.emotiwave.domain.exceptions;

public class UsuarioJaCadastrado extends RuntimeException {
    public UsuarioJaCadastrado(String message) {
        super(message);
    }
}
