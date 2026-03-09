package com.example.truckage.dtos;

import com.example.truckage.models.SolicitacaoFrete;
import java.time.LocalDateTime;

public record SolicitacaoDTO(
        Integer solicitacaoId,
        Integer empresaId,
        Integer motoristaId,
        Integer freteId,
        SolicitacaoFrete.StatusSolicitacao status,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataResposta
) {}