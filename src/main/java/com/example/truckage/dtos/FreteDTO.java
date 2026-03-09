package com.example.truckage.dtos;

import com.example.truckage.models.Frete;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FreteDTO(
        Integer freteId,

        @NotNull 
        @JsonProperty("empresa_id")  
        Integer empresaId,

        Integer motoristaId,

        @JsonProperty("data_prevista_entrega")  
        LocalDateTime dataPrevistaEntrega,

        @NotBlank 
        @JsonProperty("origem_estado")  
        String origemEstado,
        
        @NotBlank 
        @JsonProperty("origem_cidade") 
        String origemCidade,
        
        @NotBlank 
        @JsonProperty("destino_estado")  
        String destinoEstado,
        
        @NotBlank 
        @JsonProperty("destino_cidade")  
        String destinoCidade,

        @NotNull @DecimalMin("0.01") 
        @JsonProperty("valor_frete")  
        BigDecimal valorFrete,

        @JsonProperty("precisa_lona")  
        Boolean precisaLona,
        
        @JsonProperty("produto_quimico") 
        Boolean produtoQuimico,

        @JsonProperty("observacoes_motorista")  
        String observacoesMotorista,

        @JsonProperty("veiculo_tracao")  
        String veiculoTracao,
        
        @JsonProperty("tipos_carreta")  
        String tiposCarreta,
        
        @JsonProperty("comprimento_carreta")  
        String comprimentoCarreta,
        
        @JsonProperty("numero_eixos")  
        String numeroEixos,
        
        @JsonProperty("configuracao_modelo")  
        String configuracaoModelo,
        
        @JsonProperty("tipo_carga")  
        String tipoCarga,
        
        @JsonProperty("observacoes_carga")  
        String observacoesCarga,

        Frete.StatusFrete status
) {}