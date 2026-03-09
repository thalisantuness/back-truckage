package com.example.truckage.websocket;

import com.example.truckage.models.Mensagem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("usuarioId=")) {
            Integer usuarioId = Integer.parseInt(query.substring(10));
            sessions.put(usuarioId, session);
            log.info("WebSocket conectado para usuário: {}", usuarioId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Processar mensagens recebidas se necessário
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
        log.info("WebSocket desconectado");
    }

    public void enviarParaUsuario(Integer usuarioId, Object payload) {
        WebSocketSession session = sessions.get(usuarioId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(payload);
                session.sendMessage(new TextMessage(json));
                log.info("Mensagem enviada via WebSocket para usuário: {}", usuarioId);
            } catch (IOException e) {
                log.error("Erro ao enviar mensagem WebSocket", e);
            }
        }
    }
}