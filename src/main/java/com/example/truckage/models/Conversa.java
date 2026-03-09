package com.example.truckage.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversas", schema = "public")
@Data
@NoArgsConstructor
public class Conversa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversa_id")
    private Integer conversaId;

    @ManyToOne
    @JoinColumn(name = "usuario1_id", referencedColumnName = "usuario_id", nullable = false)
    private Usuario usuario1;

    @ManyToOne
    @JoinColumn(name = "usuario2_id", referencedColumnName = "usuario_id", nullable = false)
    private Usuario usuario2;

    @ManyToOne
    @JoinColumn(name = "frete_id", referencedColumnName = "frete_id", nullable = false)
    private Frete frete;

    @Column(name = "ultima_mensagem")
    private LocalDateTime ultimaMensagem;
}