package com.example.truckage.dtos;

import com.example.truckage.models.Usuario;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Map;

public record UsuarioDTO(
        // Campos básicos
        Integer usuarioId,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        @JsonProperty("nome_completo")
        @NotBlank(message = "Nome completo é obrigatório")
        String nomeCompleto,

        @NotBlank(message = "Celular é obrigatório")
        @Pattern(regexp = "^\\d{10,11}$", message = "Celular deve ter 10 ou 11 dígitos")
        String celular,

        @NotNull(message = "Role é obrigatório")
        Usuario.Role role,

        // Campos opcionais
        @JsonProperty("imagem_perfil")
        String imagemPerfil,
        Boolean habilitado,
        String cpf,
        String cnpj,
        String endereco,
        @JsonProperty("data_nascimento")
        LocalDateTime dataNascimento,
        @JsonProperty("numero_placas")
        Integer numeroPlacas,

        // Campos específicos para cadastro com imagens
        @JsonProperty("imagem_base64")
        String imagemBase64,
        Map<String, String> documentos,

        // ===== NOVOS CAMPOS PARA PLACAS (FORA DE DOCUMENTOS) =====
        @JsonProperty("placa_1")
        String placa1,

        @JsonProperty("placa_2")
        String placa2,

        @JsonProperty("placa_3")
        String placa3,

        // Campos de referências (pessoal)
        @JsonProperty("nome_referencia_pessoal_1")
        String nomeReferenciaPessoal1,
        @JsonProperty("numero_referencia_pessoal_1")
        String numeroReferenciaPessoal1,
        @JsonProperty("nome_referencia_pessoal_2")
        String nomeReferenciaPessoal2,
        @JsonProperty("numero_referencia_pessoal_2")
        String numeroReferenciaPessoal2,
        @JsonProperty("nome_referencia_pessoal_3")
        String nomeReferenciaPessoal3,
        @JsonProperty("numero_referencia_pessoal_3")
        String numeroReferenciaPessoal3,

        // Campos de referências (comercial)
        @JsonProperty("nome_referencia_comercial_1")
        String nomeReferenciaComercial1,
        @JsonProperty("numero_referencia_comercial_1")
        String numeroReferenciaComercial1,
        @JsonProperty("nome_referencia_comercial_2")
        String nomeReferenciaComercial2,
        @JsonProperty("numero_referencia_comercial_2")
        String numeroReferenciaComercial2,
        @JsonProperty("nome_referencia_comercial_3")
        String nomeReferenciaComercial3,
        @JsonProperty("numero_referencia_comercial_3")
        String numeroReferenciaComercial3,

        // Referências de motorista (para empresas)
        @JsonProperty("nome_referencia_motorista_1")
        String nomeReferenciaMotorista1,
        @JsonProperty("numero_referencia_motorista_1")
        String numeroReferenciaMotorista1,
        @JsonProperty("nome_referencia_motorista_2")
        String nomeReferenciaMotorista2,
        @JsonProperty("numero_referencia_motorista_2")
        String numeroReferenciaMotorista2,
        @JsonProperty("nome_referencia_motorista_3")
        String nomeReferenciaMotorista3,
        @JsonProperty("numero_referencia_motorista_3")
        String numeroReferenciaMotorista3,

        // Responsável administrativo (para empresas)
        @JsonProperty("nome_responsavel_administrativo")
        String nomeResponsavelAdministrativo,
        @JsonProperty("telefone_responsavel_administrativo")
        String telefoneResponsavelAdministrativo,

        // Dados bancários
        String banco,
        String agencia,
        @JsonProperty("numero_conta")
        String numeroConta,
        @JsonProperty("tipo_conta")
        String tipoConta,
        @JsonProperty("titular_conta")
        String titularConta,
        @JsonProperty("cpf_titular_conta")
        String cpfTitularConta
) {

    // Construtor compacto para criar DTO a partir de uma entidade Usuario
    public static UsuarioDTO fromEntity(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getUsuarioId(),
                usuario.getEmail(),
                null,
                usuario.getNomeCompleto(),
                usuario.getCelular(),
                usuario.getRole(),
                usuario.getImagemPerfil(),
                usuario.getHabilitado(),
                usuario.getCpf(),
                usuario.getCnpj(),
                usuario.getEndereco(),
                usuario.getDataNascimento(),
                usuario.getNumeroPlacas(),
                null,
                null,
                usuario.getPlaca1(),
                usuario.getPlaca2(),
                usuario.getPlaca3(),
                usuario.getNomeReferenciaPessoal1(),
                usuario.getNumeroReferenciaPessoal1(),
                usuario.getNomeReferenciaPessoal2(),
                usuario.getNumeroReferenciaPessoal2(),
                usuario.getNomeReferenciaPessoal3(),
                usuario.getNumeroReferenciaPessoal3(),
                usuario.getNomeReferenciaComercial1(),
                usuario.getNumeroReferenciaComercial1(),
                usuario.getNomeReferenciaComercial2(),
                usuario.getNumeroReferenciaComercial2(),
                usuario.getNomeReferenciaComercial3(),
                usuario.getNumeroReferenciaComercial3(),
                usuario.getNomeReferenciaMotorista1(),
                usuario.getNumeroReferenciaMotorista1(),
                usuario.getNomeReferenciaMotorista2(),
                usuario.getNumeroReferenciaMotorista2(),
                usuario.getNomeReferenciaMotorista3(),
                usuario.getNumeroReferenciaMotorista3(),
                usuario.getNomeResponsavelAdministrativo(),
                usuario.getTelefoneResponsavelAdministrativo(),
                usuario.getBanco(),
                usuario.getAgencia(),
                usuario.getNumeroConta(),
                usuario.getTipoConta(),
                usuario.getTitularConta(),
                usuario.getCpfTitularConta()
        );
    }
}