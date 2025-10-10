package org.example.emotiwave.web.Controller;


import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.in.MusicasSelecionadasDto;
import org.example.emotiwave.application.service.RelacionarMusicasOuvidasAoUsuario;
import org.example.emotiwave.application.service.PegarMusicasMaisOuvidas;
import org.example.emotiwave.domain.entities.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario-musica")
public class UsuarioMusicaController {
    private final PegarMusicasMaisOuvidas pegarMusicasMaisOuvidas;

    private final RelacionarMusicasOuvidasAoUsuario relacionarMusicasOuvidasAoUsuario;

    public UsuarioMusicaController(PegarMusicasMaisOuvidas pegarMusicasMaisOuvidas,  RelacionarMusicasOuvidasAoUsuario relacionarMusicasOuvidasAoUsuario1) {
        this.pegarMusicasMaisOuvidas = pegarMusicasMaisOuvidas;
        this.relacionarMusicasOuvidasAoUsuario = relacionarMusicasOuvidasAoUsuario1;


    }
    @GetMapping
    public ResponseEntity<List<MusicaSimplesDto>> pegarMusicasMaisOuvidas(@PageableDefault(size = 10) Pageable paginacao) {
        return ResponseEntity.ok(pegarMusicasMaisOuvidas.pegarMusicasMaisOuvidas(paginacao));

    }
    @PostMapping
    public void selecionarMusicas(@AuthenticationPrincipal Usuario usuario,@RequestBody MusicasSelecionadasDto spotifyTracksIds) {

        relacionarMusicasOuvidasAoUsuario.relacionarMusicasAoUsuario(spotifyTracksIds,usuario);




    }


}
