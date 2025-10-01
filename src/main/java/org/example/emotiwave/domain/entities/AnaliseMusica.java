package org.example.emotiwave.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "T_ANALISE_MUSICA")
public class AnaliseMusica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    private BigDecimal score;
    private LocalDate analisado_em;

    @OneToOne
    @JoinColumn(name = "musica_id", unique = true)
    private Musica musica;







}
