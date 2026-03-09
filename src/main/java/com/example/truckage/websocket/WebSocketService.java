package com.example.truckage.websocket;

import com.example.truckage.models.Frete;
import com.example.truckage.models.Mensagem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final WebSocketHandler webSocketHandler;

    public void enviarMensagem(Mensagem mensagem) {
        Integer destinatarioId = mensagem.getConversa().getUsuario1().getUsuarioId()
                .equals(mensagem.getRemetente().getUsuarioId())
                ? mensagem.getConversa().getUsuario2().getUsuarioId()
                : mensagem.getConversa().getUsuario1().getUsuarioId();

        webSocketHandler.enviarParaUsuario(destinatarioId, mensagem);
    }

    public void notificarNovoFrete(Frete frete) {
        // Enviar notificação para todos os motoristas online
        // Isso seria implementado com um broadcast para todas as sessões de motoristas
    }
}