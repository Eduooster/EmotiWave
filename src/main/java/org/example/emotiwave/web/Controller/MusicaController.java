package org.example.emotiwave.web.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.emotiwave.application.dto.in.BuscarMusicaDto;

import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.out.MusicasMaisOuvidasResponseDto;
import org.example.emotiwave.application.service.MusicaService;
import org.example.emotiwave.domain.entities.Musica;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/musicas")
@Tag(name = "Musica", description = "Gerenciamento do musica")
public class MusicaController {

    private final MusicaService musicaService;


    public MusicaController(MusicaService musicaService) {
        this.musicaService = musicaService;
    }

    @Operation(
            summary = "Listar músicas mais ouvidas",
            description = "Retorna uma página das músicas mais ouvidas no sistema, ordenadas pela popularidade."
    )
    @ApiResponse(responseCode = "200", description = "Lista de músicas retornada com sucesso")
    @GetMapping("/top")
    public ResponseEntity<Page<MusicaSimplesDto>> listar(
            @PageableDefault(size = 10) Pageable paginacao) {

        var page = musicaService.listarMusicasMaisOuvidas(paginacao);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Buscar música no Spotify",
            description = "Busca uma música pelo artista e título na API do Spotify. Retorna 404 caso não encontrada."
    )
    @ApiResponse(responseCode = "200", description = "Música encontrada e retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Música não encontrada")
    @GetMapping("/spotify/buscar")
    public ResponseEntity<Musica> buscarMusicaNoSpotify(
            @RequestParam String artista,
            @RequestParam String titulo) {

        Musica musica = musicaService.buscarMusica(artista, titulo);
        return ResponseEntity.ok(musica);
    }






}
