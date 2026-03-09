package com.example.truckage.dtos;

import java.time.LocalDateTime;

public record ConversaDTO(
        Integer conversaId,
        Integer usuario1Id,
        Integer usuario2Id,
        Integer freteId,
        LocalDateTime ultimaMensagem,
        UsuarioBasicoDTO usuario1,
        UsuarioBasicoDTO usuario2,
        FreteBasicoDTO frete
) {}