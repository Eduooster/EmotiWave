package org.example.emotiwave.web.Controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.emotiwave.application.service.spotifyServices.SpotifyService;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.infra.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/spotify")
@Tag(name = "SpotifyAuth", description = "Gerenciamento da autenticaçãp com o  spotify")
public class SpotifyController {


    private final SpotifyService spotifyService;
    private final TokenService tokenService;



    public SpotifyController(SpotifyService spotifyService, TokenService tokenService ) {
        this.spotifyService = spotifyService;
        this.tokenService = tokenService;

    }

    @Operation(
            summary = "Redirecionar usuário para login do Spotify",
            description = "Gera a URL de autorização do Spotify para o usuário se conectar à sua conta. É necessário passar o token JWT no header Authorization."
    )
    @ApiResponse(responseCode = "200", description = "URL de autorização gerada com sucesso")
    @GetMapping("/auth")
    public ResponseEntity<String> redirecionarParaLoginSpotify(@RequestHeader("Authorization") String authHeader) {
        String authUrl = spotifyService.solicitarAutorizacao(authHeader);
        return ResponseEntity.ok(authUrl);
    }

    @Operation(
            summary = "Callback do Spotify após login",
            description = "Recebe o código de autorização do Spotify e o token JWT do usuário (state) para trocar pelo access token. Atualiza os tokens do usuário no sistema."
    )
    @ApiResponse(responseCode = "200", description = "Código de autorização recebido e processado com sucesso")
    @GetMapping("/callback")
    public ResponseEntity<?> spotifyCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String jwt) {
        Usuario usuario = tokenService.getUsuarioFromToken(jwt);
        spotifyService.exchangeCodeForTokens(code,usuario);
        return ResponseEntity.ok(code);
   }






}
