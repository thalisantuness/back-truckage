package com.example.truckage.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes_frete", schema = "public")
@Data
@NoArgsConstructor
public class SolicitacaoFrete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solicitacao_id")
    private Integer solicitacaoId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", referencedColumnName = "usuario_id", nullable = false)
    private Usuario empresa;

    @ManyToOne
    @JoinColumn(name = "motorista_id", referencedColumnName = "usuario_id", nullable = false)
    private Usuario motorista;

    @ManyToOne
    @JoinColumn(name = "frete_id", referencedColumnName = "frete_id", nullable = false)
    private Frete frete;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status = StatusSolicitacao.pendente;

    @Column(name = "data_solicitacao")
    private LocalDateTime dataSolicitacao = LocalDateTime.now();

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    public enum StatusSolicitacao {
        pendente, aceita, rejeitada
    }
}