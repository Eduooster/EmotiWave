package org.example.emotiwave.application.service;

import org.example.emotiwave.domain.entities.AnaliseMusica;
import org.example.emotiwave.domain.entities.Musica;
import org.example.emotiwave.domain.exceptions.HuggingFaceException;
import org.example.emotiwave.infra.client.HuggingFaceZeroShotClient;
import org.example.emotiwave.infra.repository.AnaliseMusicaRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class HuggingFaceZeroShotService {
    private final AnaliseMusicaRepository analiseMusicaRepository;

    private final HuggingFaceZeroShotClient huggingFaceZeroShotClient;

    public HuggingFaceZeroShotService(AnaliseMusicaRepository analiseMusicaRepository, HuggingFaceZeroShotClient huggingFaceZeroShotClient) {
        this.analiseMusicaRepository = analiseMusicaRepository;

        this.huggingFaceZeroShotClient = huggingFaceZeroShotClient;
    }


    public AnaliseMusica analisarMusica(Musica musica) throws IOException, HuggingFaceException {
        ArrayList<Serializable> responseParseado = huggingFaceZeroShotClient.obterAnalise(musica);

        AnaliseMusica analise = new AnaliseMusica();
        analise.setLabel((String) responseParseado.get(0));
        analise.setScore((BigDecimal) responseParseado.get(1));
        analise.setAnalisado_em(LocalDate.now());
        analise.setMusica(musica);

        analiseMusicaRepository.save(analise);

        return analise;


    }
}
