package org.example.emotiwave.web.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.emotiwave.application.dto.out.DadosTokenJwtResponseDto;
import org.example.emotiwave.application.service.AutenticacaoService;
import org.example.emotiwave.application.dto.in.DadoAuthRequestRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Gerenciamento de autenticações")
public class AutenticacaoController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @PostMapping
    public ResponseEntity efetuarAutenticacao(@RequestBody @Valid DadoAuthRequestRequestDto dados) {
        String tokenJWT = autenticacaoService.autenticar(dados);

        return ResponseEntity.ok(new DadosTokenJwtResponseDto(tokenJWT));
    }


}