package org.example.emotiwave.web.Controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.service.PegarMusicasMaisOuvidasService;
import org.example.emotiwave.application.service.SpotifyService;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.infra.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spotify")
@Tag(name = "Spotify", description = "Gerenciamento do spotify")
public class SpotifyController {

    private final PegarMusicasMaisOuvidasService pegarMusicasMaisOuvidasService;
    private final SpotifyService spotifyService;
    private final TokenService tokenService;


    public SpotifyController(PegarMusicasMaisOuvidasService pegarMusicasMaisOuvidasService, SpotifyService spotifyService, TokenService tokenService) {
        this.pegarMusicasMaisOuvidasService = pegarMusicasMaisOuvidasService;
        this.spotifyService = spotifyService;
        this.tokenService = tokenService;
    }

    @GetMapping("/auth")
    public ResponseEntity<String> redirecaoParaSpotifyLogin(@RequestHeader("Authorization") String authHeader) {
        String authUrl = spotifyService.solicitarAutorizacao(authHeader);
        return ResponseEntity.ok(authUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> spotifyCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String jwt) {
        Usuario usuario = tokenService.getUsuarioFromToken(jwt);
        spotifyService.exchangeCodeForTokens(code,usuario);
        return ResponseEntity.ok(code);
   }

   @GetMapping("/user-top-read")
    public ResponseEntity userTopRead(@AuthenticationPrincipal Usuario usuario)
    {
        ResponseEntity<List<MusicaSimplesDto>> response = pegarMusicasMaisOuvidasService.pegarMusicasMaisOuvidas(usuario);
        return ResponseEntity.ok(response);
    }

}
