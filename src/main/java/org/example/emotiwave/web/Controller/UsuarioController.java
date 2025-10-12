package org.example.emotiwave.web.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.emotiwave.application.dto.in.UsuarioCreateRequestDto;

import org.example.emotiwave.application.dto.out.UsuarioDetailResponseDto;

import org.example.emotiwave.application.dto.out.UsuarioListResponseDto;
import org.example.emotiwave.application.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuario", description = "Gerenciamento de usuarios")
public class UsuarioController {


    @Autowired
    private UsuarioService usuarioService;

    @Operation(
            summary = "Criar um novo usuário",
            description = "Recebe os dados de cadastro do usuário e cria um novo registro no sistema. Retorna o usuário criado com o ID gerado."
    )
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @PostMapping
    public ResponseEntity<UsuarioDetailResponseDto> criar(@RequestBody @Valid UsuarioCreateRequestDto dto, UriComponentsBuilder uriBuilder) {

        UsuarioDetailResponseDto cadastroNovoUsuario = usuarioService.cadastrar(dto);
        var uri = uriBuilder.path("/usuario/{id}").buildAndExpand(cadastroNovoUsuario.id()).toUri();
        return ResponseEntity.created(uri).body(cadastroNovoUsuario);
    }

    @Operation(
            summary = "Listar usuários",
            description = "Retorna uma lista paginada de usuários do sistema."
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<UsuarioDetailResponseDto>> listar(
            @PageableDefault(size = 10, sort = { "username" }) Pageable paginacao) {

        var page = usuarioService.listar(paginacao);

        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Excluir usuário",
            description = "Exclui um usuário existente pelo ID e retorna os dados do usuário excluído."
    )
    @ApiResponse(responseCode = "200", description = "Usuário excluído com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDetailResponseDto> excluir(@PathVariable Long id) {
        UsuarioDetailResponseDto usuarioDeletado = usuarioService.excluir(id);
        return ResponseEntity.ok(usuarioDeletado);
    }

    @Operation(
            summary = "Detalhar usuário",
            description = "Retorna os detalhes de um usuário pelo ID."
    )
    @ApiResponse(responseCode = "200", description = "Detalhes do usuário retornados com sucesso")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDetailResponseDto> detalhar(@PathVariable Long id) {
        UsuarioDetailResponseDto dto = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }



}
