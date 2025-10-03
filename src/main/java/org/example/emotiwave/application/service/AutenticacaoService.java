package org.example.emotiwave.application.service;

import jakarta.validation.Valid;
import org.example.emotiwave.application.dto.in.DadoAuthRequestRequestDto;
import org.example.emotiwave.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String autenticar(@Valid DadoAuthRequestRequestDto dados) {

        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.password());

        var tokenJwt =tokenService.gerarToken((UserDetails) authenticationManager.authenticate(authenticationToken).getPrincipal());

        return tokenJwt;
    }
}