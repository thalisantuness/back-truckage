package com.example.truckage.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fretes", schema = "public")
@Data
@NoArgsConstructor
public class Frete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "frete_id")
    private Integer freteId;

    @ManyToOne
    @JoinColumn(name = "empresa_id", referencedColumnName = "usuario_id", nullable = false)
    private Usuario empresa;

    @ManyToOne
    @JoinColumn(name = "motorista_id", referencedColumnName = "usuario_id")
    private Usuario motorista;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_prevista_entrega")
    private LocalDateTime dataPrevistaEntrega;

    @Column(name = "origem_estado")
    private String origemEstado;
    @Column(name = "origem_cidade")
    private String origemCidade;
    @Column(name = "destino_estado")
    private String destinoEstado;
    @Column(name = "destino_cidade")
    private String destinoCidade;

    @Column(name = "valor_frete", precision = 10, scale = 2)
    private BigDecimal valorFrete;

    @Column(name = "precisa_lona")
    private Boolean precisaLona;
    @Column(name = "produto_quimico")
    private Boolean produtoQuimico;

    @Column(name = "observacoes_motorista", columnDefinition = "TEXT")
    private String observacoesMotorista;

    @Column(name = "veiculo_tracao")
    private String veiculoTracao;
    @Column(name = "tipos_carreta")
    private String tiposCarreta;
    @Column(name = "comprimento_carreta")
    private String comprimentoCarreta;
    @Column(name = "numero_eixos")
    private String numeroEixos;
    @Column(name = "configuracao_modelo")
    private String configuracaoModelo;
    @Column(name = "tipo_carga")
    private String tipoCarga;
    @Column(name = "observacoes_carga", columnDefinition = "TEXT")
    private String observacoesCarga;

    @Enumerated(EnumType.STRING)
    private StatusFrete status = StatusFrete.anunciado;

    public enum StatusFrete {
        anunciado, em_andamento, finalizado, cancelado
    }
}