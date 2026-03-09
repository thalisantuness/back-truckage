package com.example.truckage.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens", schema = "public")
@Data
@NoArgsConstructor
public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mensagem_id")
    private Integer mensagemId;

    @ManyToOne
    @JoinColumn(name = "conversa_id", referencedColumnName = "conversa_id", nullable = false)
    private Conversa conversa;

    @ManyToOne
    @JoinColumn(name = "remetente_id", referencedColumnName = "usuario_id", nullable = false)
    private Usuario remetente;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio = LocalDateTime.now();

    private Boolean lida = false;
}