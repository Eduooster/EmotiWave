package org.example.emotiwave.domain.exceptions;

public class AutenticacaoFalhou extends RuntimeException {
    public AutenticacaoFalhou(String message) {
        super(message);
    }
}
