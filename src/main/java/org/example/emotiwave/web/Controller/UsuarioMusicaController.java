package org.example.emotiwave.web.Controller;


import io.swagger.v3.oas.annotations.Operation;
import org.example.emotiwave.application.dto.in.MusicaSelecionadaDto;
import org.example.emotiwave.application.dto.in.MusicaSimplesDto;
import org.example.emotiwave.application.dto.in.MusicasSelecionadasDto;
import org.example.emotiwave.application.service.UsuarioMusicaServices.AssociarMusicasAoUsuarioService;
import org.example.emotiwave.application.service.MusicasMaisOuvidasUsuarioManualService;
import org.example.emotiwave.application.service.UsuarioMusicaServices.UsuarioMusicaService;
import org.example.emotiwave.application.service.spotifyServices.SpotifyMusicasRecentesService;
import org.example.emotiwave.application.service.spotifyServices.TopMusicasSpotifyService;
import org.example.emotiwave.domain.entities.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioMusicaController {
    private final MusicasMaisOuvidasUsuarioManualService pegarMusicasMaisOuvidas;

    private final AssociarMusicasAoUsuarioService relacionarMusicasOuvidasAoUsuario;
    private final TopMusicasSpotifyService topMusicasSpotifyService;
    private final SpotifyMusicasRecentesService spotifyMusicasRecentesService;
    private final UsuarioMusicaService usuarioMusicaService;



    public UsuarioMusicaController(MusicasMaisOuvidasUsuarioManualService pegarMusicasMaisOuvidas, AssociarMusicasAoUsuarioService relacionarMusicasOuvidasAoUsuario1, TopMusicasSpotifyService topMusicasSpotifyService, SpotifyMusicasRecentesService spotifyMusicasRecentesService, UsuarioMusicaService usuarioMusicaService) {
        this.pegarMusicasMaisOuvidas = pegarMusicasMaisOuvidas;
        this.relacionarMusicasOuvidasAoUsuario = relacionarMusicasOuvidasAoUsuario1;


        this.topMusicasSpotifyService = topMusicasSpotifyService;
        this.spotifyMusicasRecentesService = spotifyMusicasRecentesService;
        this.usuarioMusicaService = usuarioMusicaService;

    }

    @Operation(summary = "Listar músicas recentes do usuário sem Spotify",
            description = "Retorna as músicas que o usuário escutou hoje ou recentemente, para usuários que não possuem Spotify associado.")
    @GetMapping("/musicas/sem-spotify")
    public ResponseEntity<List<MusicaSimplesDto>> musicasOuvidasRecentemente(@PageableDefault(size = 10) Pageable paginacao,@AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(pegarMusicasMaisOuvidas.musicasRecemOuvidas(paginacao,usuario));

    }
    @Operation(
            summary = "Salvar preferências musicais do usuário",
            description = "Registra as músicas selecionadas pelo usuário sem spotify, criando ou atualizando os relacionamentos no banco de dados entre usuário e música."
    )
    @PostMapping("/musicas/preferencias")
    public ResponseEntity salvarPreferenciasMusicais(@AuthenticationPrincipal Usuario usuario,@RequestBody MusicasSelecionadasDto musicasSelecionadasDto) {
        relacionarMusicasOuvidasAoUsuario.processarRelacionamentos(musicasSelecionadasDto,usuario);
        return ResponseEntity.noContent().build();
    }
    @Operation(
            summary = "Listar top músicas do usuário via Spotify",
            description = "Busca as músicas mais ouvidas pelo usuário através da integração com Spotify e retorna a lista de forma paginada, registrando os dados no banco quando necessário."
    )

    @GetMapping("/musicas/spotify/top")
    public ResponseEntity userTopRead(@AuthenticationPrincipal Usuario usuario)
    {
        List<MusicaSimplesDto> response = topMusicasSpotifyService.buscarTopMusicasSpotify(usuario);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar músicas recentemente tocadas via Spotify",
            description = "Retorna as músicas que o usuário escutou recentemente no Spotify, chamando a API do Spotify e registrando os dados no banco se necessário."
    )
    @GetMapping("/musicas/spotify/recentes")
    public ResponseEntity buscaMusicasRecentesSpotify(@AuthenticationPrincipal Usuario usuario) throws IOException, InterruptedException {

        ResponseEntity<List<MusicaSimplesDto>> response = spotifyMusicasRecentesService.buscarMusicasOuvidasRecentes(usuario);
        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Desvincular música do usuário",
            description = "Remove a associação de uma música específica com o usuário, sem deletar a música do sistema global."
    )
    @DeleteMapping("/musicas/preferencias/{musicaId}")
    public ResponseEntity delete(@AuthenticationPrincipal Usuario usuario,@PathVariable String musicaId) {
        usuarioMusicaService.desvincular(usuario,musicaId);

        return ResponseEntity.noContent().build();

    }

    @Operation(
            summary = "Marcar música como selecionada pelo usuário",
            description = "Atualiza o relacionamento entre o usuário e a música, permitindo marcar ou desmarcar como selecionada."
    )

    @PatchMapping("/musicas/preferencias/musica/{id}")
    public ResponseEntity selecionarMusica(@AuthenticationPrincipal Usuario usuario, @PathVariable String id, @RequestBody
    MusicaSelecionadaDto musicaSelecionadaDto) {
        usuarioMusicaService.marcarComoSelecionada(usuario, id, musicaSelecionadaDto);
        return ResponseEntity.noContent().build();

    }
}
