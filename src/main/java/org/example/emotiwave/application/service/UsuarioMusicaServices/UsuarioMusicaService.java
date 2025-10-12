package org.example.emotiwave.application.service.UsuarioMusicaServices;

import org.example.emotiwave.application.dto.in.MusicaSelecionadaDto;
import org.example.emotiwave.domain.entities.Musica;
import org.example.emotiwave.domain.entities.Usuario;
import org.example.emotiwave.domain.entities.UsuarioMusica;
import org.example.emotiwave.domain.exceptions.ErroAoDesvincularMusicaAoUsuario;
import org.example.emotiwave.domain.exceptions.MusicaNaoEcontrada;
import org.example.emotiwave.infra.repository.MusicaRepository;
import org.example.emotiwave.infra.repository.UsuarioMusicaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UsuarioMusicaService {
    private final UsuarioMusicaRepository usuarioMusicaRepository;
    private final MusicaRepository musicaRepository;

    public UsuarioMusicaService(UsuarioMusicaRepository usuarioMusicaRepository, MusicaRepository musicaRepository) {
        this.usuarioMusicaRepository = usuarioMusicaRepository;
        this.musicaRepository = musicaRepository;
    }

    public void desvincular(Usuario usuario, String musicaId) {
        UsuarioMusica musicaRepo= usuarioMusicaRepository.findByMusica_SpotifyTrackIdAndUsuarioId(musicaId,usuario.getId());

        if (musicaRepo == null) {
            throw new MusicaNaoEcontrada("Musica vinculada ao usuario nao encontrada!");
        }
        if (musicaRepo.isSelecionada()){
            musicaRepo.setSelecionada(Boolean.FALSE);
            usuarioMusicaRepository.save(musicaRepo);
        }
        else{
            throw new ErroAoDesvincularMusicaAoUsuario("Musica já desvinculada");
        }

    }

    public void marcarComoSelecionada(Usuario usuario, String spotifyTrackId, MusicaSelecionadaDto musicaSelecionadaDto) {
        UsuarioMusica usuarioMusicaRepo= usuarioMusicaRepository.findByUsuarioIdAndMusicaSpotifyTrackId(usuario.getId(),spotifyTrackId);

        Musica musica = musicaRepository.findBySpotifyTrackId(spotifyTrackId);

        if (usuarioMusicaRepo == null) {
            UsuarioMusica usuarioMusica = new UsuarioMusica();
            usuarioMusica.setUsuario(usuario);
            usuarioMusica.setMusica(musica);
            usuarioMusica.setSelecionada(true);
            usuarioMusica.setOuvidaEm(musicaSelecionadaDto.ouvidaHoje()? LocalDate.now():null);
            usuarioMusicaRepository.save(usuarioMusica);
        }else{
            usuarioMusicaRepo.setOuvidaEm(musicaSelecionadaDto.ouvidaHoje()? LocalDate.now():null);
        }



    }
}
