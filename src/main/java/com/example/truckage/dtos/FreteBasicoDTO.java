package com.example.truckage.dtos;

import java.math.BigDecimal;

public record FreteBasicoDTO(
        Integer freteId,
        String origemCidade,
        String origemEstado,
        String destinoCidade,
        String destinoEstado,
        BigDecimal valorFrete,
        String status
) {}