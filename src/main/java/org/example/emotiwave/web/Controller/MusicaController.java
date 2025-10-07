package org.example.emotiwave.web.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.emotiwave.application.dto.in.MusicasCurtidasRequest;
import org.example.emotiwave.application.dto.out.MusicasMaisOuvidasResponseDto;
import org.example.emotiwave.application.service.MusicaService;
import org.example.emotiwave.domain.entities.Musica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/musica")
@Tag(name = "Musica", description = "Gerenciamento do musica")
public class MusicaController {

    private final MusicaService musicaService;


    public MusicaController(MusicaService musicaService) {
        this.musicaService = musicaService;
    }

    @PostMapping
    public ResponseEntity curtirMusicas(@RequestBody MusicasCurtidasRequest musicasCurtidasRequest) {
        musicaService.adionarMusicasCurtidas(musicasCurtidasRequest);
        return ResponseEntity.ok().build();

    };

    public ResponseEntity<Page<MusicasMaisOuvidasResponseDto>> listar(
            @PageableDefault(size = 10) Pageable paginacao) {

        var page = musicaService.listarMusicasMaisOuvidas(paginacao);
        return ResponseEntity.ok(page);
    }


}
