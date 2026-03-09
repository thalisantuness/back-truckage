package com.example.truckage.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios", schema = "public")
@Data
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, length = 11)
    private String celular;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "imagem_perfil")
    private String imagemPerfil;

    private Boolean habilitado = false;

    private String cpf;
    private String cnpj;
    private String endereco;

    @Column(name = "data_nascimento")
    private LocalDateTime dataNascimento;

    @Column(name = "numero_placas")
    private Integer numeroPlacas;

    // Documentos
    private String antt;
    private String cnh;
    private String placa1;
    private String placa2;
    private String placa3;
    private String alvara;
    private String comprovanteEmpresa;
    private String documentoEmpresa;
    private String comprovanteResidenciaMotorista;
    private String documentoDonoCaminhao;
    private String comprovanteResidenciaDonoCaminhao;

    // Referências Pessoais
    @Column(name = "nome_referencia_pessoal_1")
    private String nomeReferenciaPessoal1;
    @Column(name = "numero_referencia_pessoal_1")
    private String numeroReferenciaPessoal1;
    @Column(name = "nome_referencia_pessoal_2")
    private String nomeReferenciaPessoal2;
    @Column(name = "numero_referencia_pessoal_2")
    private String numeroReferenciaPessoal2;
    @Column(name = "nome_referencia_pessoal_3")
    private String nomeReferenciaPessoal3;
    @Column(name = "numero_referencia_pessoal_3")
    private String numeroReferenciaPessoal3;

    // Referências Comerciais
    @Column(name = "nome_referencia_comercial_1")
    private String nomeReferenciaComercial1;
    @Column(name = "numero_referencia_comercial_1")
    private String numeroReferenciaComercial1;
    @Column(name = "nome_referencia_comercial_2")
    private String nomeReferenciaComercial2;
    @Column(name = "numero_referencia_comercial_2")
    private String numeroReferenciaComercial2;
    @Column(name = "nome_referencia_comercial_3")
    private String nomeReferenciaComercial3;
    @Column(name = "numero_referencia_comercial_3")
    private String numeroReferenciaComercial3;

    // ===== NOVOS CAMPOS: Referências de Motorista (para empresas) =====
    @Column(name = "nome_referencia_motorista_1")
    private String nomeReferenciaMotorista1;
    @Column(name = "numero_referencia_motorista_1")
    private String numeroReferenciaMotorista1;
    @Column(name = "nome_referencia_motorista_2")
    private String nomeReferenciaMotorista2;
    @Column(name = "numero_referencia_motorista_2")
    private String numeroReferenciaMotorista2;
    @Column(name = "nome_referencia_motorista_3")
    private String nomeReferenciaMotorista3;
    @Column(name = "numero_referencia_motorista_3")
    private String numeroReferenciaMotorista3;

    // Responsável Administrativo
    @Column(name = "nome_responsavel_administrativo")
    private String nomeResponsavelAdministrativo;
    @Column(name = "telefone_responsavel_administrativo")
    private String telefoneResponsavelAdministrativo;

    // Dados bancários
    private String banco;
    private String agencia;
    @Column(name = "numero_conta")
    private String numeroConta;
    @Column(name = "tipo_conta")
    private String tipoConta;
    @Column(name = "titular_conta")
    private String titularConta;
    @Column(name = "cpf_titular_conta")
    private String cpfTitularConta;

    // Referências de Transportadora (se precisar)
    @Column(name = "nome_referencia_transportadora_1")
    private String nomeReferenciaTransportadora1;
    @Column(name = "numero_referencia_transportadora_1")
    private String numeroReferenciaTransportadora1;
    @Column(name = "nome_referencia_transportadora_2")
    private String nomeReferenciaTransportadora2;
    @Column(name = "numero_referencia_transportadora_2")
    private String numeroReferenciaTransportadora2;
    @Column(name = "nome_referencia_transportadora_3")
    private String nomeReferenciaTransportadora3;
    @Column(name = "numero_referencia_transportadora_3")
    private String numeroReferenciaTransportadora3;

    @Column(name = "push_token")
    private String pushToken;

    @Column(name = "password_reset_token")
    private String passwordResetToken;
    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;

    public enum Role {
        motorista, empresa, admin
    }
}