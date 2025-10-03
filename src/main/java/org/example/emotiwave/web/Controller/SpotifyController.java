package org.example.emotiwave.web.Controller;


import org.example.emotiwave.application.service.AutenticacaoService;
import org.example.emotiwave.application.service.PegarMusicasMaisOuvidas;
import org.example.emotiwave.application.service.SpotifyAuthService;
import org.example.emotiwave.application.service.UsuarioSpotifyTokenService;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.infra.security.TokenService;
import org.springdoc.core.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    private final PegarMusicasMaisOuvidas pegarMusicasMaisOuvidas;
    private final SpotifyAuthService spotifyAuthService;


    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private TokenService tokenService;

    public SpotifyController(PegarMusicasMaisOuvidas pegarMusicasMaisOuvidas, SpotifyAuthService spotifyAuthService) {
        this.pegarMusicasMaisOuvidas = pegarMusicasMaisOuvidas;
        this.spotifyAuthService = spotifyAuthService;

    }

    @GetMapping("/auth")
    public ResponseEntity<String> redirecaoParaSpotifyLogin(@RequestHeader("Authorization") String authHeader) {
        String authUrl = spotifyAuthService.contruirAutorizacao(authHeader);
        return ResponseEntity.ok(authUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> spotifyCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String jwt) {
        Usuario usuario = tokenService.getUsuarioFromToken(jwt);
        Map<String, Object> tokens = spotifyAuthService.exchangeCodeForTokens(code,usuario);
        return ResponseEntity.ok(code);
   }

   @GetMapping("/user-top-read")
    public ResponseEntity userTopRead(@AuthenticationPrincipal Usuario usuario)
    {
        ResponseEntity<String> response = pegarMusicasMaisOuvidas.pegarMusicasMaisOuvidas(usuario);
        return ResponseEntity.ok(response);
    }

}
