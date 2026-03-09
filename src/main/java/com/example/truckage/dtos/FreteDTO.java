package com.example.truckage.dtos;

import com.example.truckage.models.Frete;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FreteDTO(
        Integer freteId,

        @NotNull Integer empresaId,

        Integer motoristaId,

        LocalDateTime dataPrevistaEntrega,

        @NotBlank String origemEstado,
        @NotBlank String origemCidade,
        @NotBlank String destinoEstado,
        @NotBlank String destinoCidade,

        @NotNull @DecimalMin("0.01") BigDecimal valorFrete,

        Boolean precisaLona,
        Boolean produtoQuimico,

        String observacoesMotorista,

        String veiculoTracao,
        String tiposCarreta,
        String comprimentoCarreta,
        String numeroEixos,
        String configuracaoModelo,
        String tipoCarga,
        String observacoesCarga,

        Frete.StatusFrete status
) {}