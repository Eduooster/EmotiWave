package org.example.emotiwave.domain.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "T_USUARIO_MUSICA")
public class UsuarioMusica
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    private Usuario usuario;
    @ManyToOne
    private Musica musica;

    private LocalDate ouvida_em;



}
