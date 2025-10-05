package org.example.emotiwave.web.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.emotiwave.application.dto.in.UsuarioCreateRequestDto;
import org.example.emotiwave.application.dto.out.ListaUsuarioResponseDto;
import org.example.emotiwave.application.dto.out.UsuarioDetailsResponseDto;
import org.example.emotiwave.application.service.CrudUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public ResponseEntity<Page<ListaUsuarioResponseDto>> listar(
            @PageableDefault(size = 10, sort = { "username" }) Pageable paginacao) {

        var page = crudUsuarioService.listar(paginacao);

        return ResponseEntity.ok(page);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDetailsResponseDto> delete(@PathVariable Long id) {
        UsuarioDetailsResponseDto usuarioDeletado = crudUsuarioService.deletar(id);
        return ResponseEntity.ok(usuarioDeletado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDetailsResponseDto> detalhar(@PathVariable Long id) {
        UsuarioDetailsResponseDto  dto = crudUsuarioService.detalhar(id);
        return ResponseEntity.ok(dto);
    }



}
