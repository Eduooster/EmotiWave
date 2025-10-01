package org.example.emotiwave.web.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.emotiwave.application.dto.in.UsuarioCreateRequestDto;
import org.example.emotiwave.application.dto.out.UsuarioDetailsResponseDto;
import org.example.emotiwave.application.service.CrudUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuario", description = "Gerenciamento de usuarios")
public class UsuarioController {

    @Autowired
    private CrudUsuarioService crudUsuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDetailsResponseDto> create(@RequestBody @Valid UsuarioCreateRequestDto dto, UriComponentsBuilder uriBuilder) {

        UsuarioDetailsResponseDto cadastroNovoUsuario = crudUsuarioService.cadastrar(dto);
        var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(cadastroNovoUsuario.id()).toUri();
        return ResponseEntity.created(uri).body(cadastroNovoUsuario);
    }

}
