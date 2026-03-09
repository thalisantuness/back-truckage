package com.example.truckage.dtos;

import java.time.LocalDateTime;

public record MensagemDTO(
        Integer mensagemId,
        Integer conversaId,
        Integer remetenteId,
        String conteudo,
        LocalDateTime dataEnvio,
        Boolean lida,
        UsuarioBasicoDTO remetente
) {}