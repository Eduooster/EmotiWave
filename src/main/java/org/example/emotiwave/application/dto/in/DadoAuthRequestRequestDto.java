package org.example.emotiwave.application.dto.in;

import java.io.Serializable;


public record DadoAuthRequestRequestDto(String email, String password) implements Serializable {}
