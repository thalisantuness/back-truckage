package com.example.truckage.controllers;

import com.example.truckage.dtos.MensagemDTO;
import com.example.truckage.models.Conversa;
import com.example.truckage.models.Mensagem;
import com.example.truckage.models.Usuario;
import com.example.truckage.services.ChatService;
import com.example.truckage.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final WebSocketService webSocketService;

    @GetMapping("/conversas")
    public ResponseEntity<List<Conversa>> listarConversas(
            @AuthenticationPrincipal Usuario usuarioLogado) {

        List<Conversa> conversas = chatService.listarConversas(usuarioLogado.getUsuarioId());
        return ResponseEntity.ok(conversas);
    }

    @GetMapping("/conversas/{conversaId}/mensagens")
    public ResponseEntity<List<Mensagem>> listarMensagens(
            @PathVariable Integer conversaId,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        List<Mensagem> mensagens = chatService.listarMensagens(conversaId, usuarioLogado.getUsuarioId());
        return ResponseEntity.ok(mensagens);
    }

    @PostMapping("/conversas")
    public ResponseEntity<?> criarConversa(
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Integer destinatarioId = body.get("destinatario_id");
        Integer freteId = body.get("frete_id");

        Conversa conversa = chatService.criarConversaSeNaoExistir(
                usuarioLogado.getUsuarioId(), destinatarioId, freteId);

        return ResponseEntity.status(201).body(Map.of(
                "message", "Conversa criada com sucesso",
                "conversa", conversa
        ));
    }

    @PostMapping("/conversas/{conversaId}/mensagens")
    public ResponseEntity<Mensagem> enviarMensagem(
            @PathVariable Integer conversaId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Mensagem mensagem = chatService.enviarMensagem(
                conversaId,
                usuarioLogado.getUsuarioId(),
                body.get("conteudo")
        );

        // Enviar via WebSocket
        webSocketService.enviarMensagem(mensagem);

        return ResponseEntity.status(201).body(mensagem);
    }

    @PutMapping("/mensagens/{mensagemId}/lida")
    public ResponseEntity<?> marcarComoLida(
            @PathVariable Integer mensagemId,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        chatService.marcarMensagemComoLida(mensagemId, usuarioLogado.getUsuarioId());
        return ResponseEntity.ok(Map.of("message", "Mensagem marcada como lida"));
    }
}