package org.example.emotiwave.application.service;

import jakarta.validation.Valid;


import org.example.emotiwave.application.dto.in.DadosAuthRequestDto;
import org.example.emotiwave.domain.exceptions.AutenticacaoFalhou;
import org.example.emotiwave.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String autenticar(@Valid DadosAuthRequestDto dados) {

        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.password());

            var userDetails = (UserDetails) authenticationManager.authenticate(authenticationToken).getPrincipal();
            var tokenJwt = tokenService.gerarToken(userDetails);

            return tokenJwt;

        } catch (AuthenticationException ex) {

            throw new AutenticacaoFalhou("Usuário ou senha inválidos");
        }
    }
}