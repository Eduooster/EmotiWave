package org.example.emotiwave.web.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.emotiwave.application.service.MusicaService;
import org.example.emotiwave.domain.entities.Musica;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/musica")
@Tag(name = "Musica", description = "Gerenciamento do musica")
public class MusicaController {

    private final MusicaService musicaService;


    public MusicaController(MusicaService musicaService) {
        this.musicaService = musicaService;
    }
}
