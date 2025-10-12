package org.example.emotiwave.application.dto.in;

import java.io.Serializable;


public record DadosAuthRequestDto(String email, String password) implements Serializable {}
