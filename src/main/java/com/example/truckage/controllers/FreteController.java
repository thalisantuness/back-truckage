package com.example.truckage.controllers;

import com.example.truckage.dtos.FreteDTO;
import com.example.truckage.models.Frete;
import com.example.truckage.models.Usuario;
import com.example.truckage.services.FreteService;
import com.example.truckage.websocket.WebSocketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fretes")
@RequiredArgsConstructor
public class FreteController {

    private final FreteService freteService;
    private final WebSocketService webSocketService;

    @PostMapping
    public ResponseEntity<Frete> criarFrete(
            @Valid @RequestBody FreteDTO freteDTO,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Frete frete = freteService.criarFrete(freteDTO, usuarioLogado.getUsuarioId());

        // Notificar via WebSocket sobre novo frete disponível
        if (frete.getStatus() == Frete.StatusFrete.anunciado) {
            webSocketService.notificarNovoFrete(frete);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(frete);
    }

    @GetMapping
    public ResponseEntity<List<Frete>> listarFretes(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @ModelAttribute FreteDTO filtros) {

        List<Frete> fretes = freteService.listarFretes(usuarioLogado, filtros);
        return ResponseEntity.ok(fretes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Frete> buscarPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Frete frete = freteService.buscarPorId(id);

        // Verificar permissão manualmente
        if (!verificarPermissaoVisualizacao(frete, usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(frete);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Frete> atualizarFrete(
            @PathVariable Integer id,
            @Valid @RequestBody FreteDTO freteDTO,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Frete frete = freteService.atualizarFrete(id, freteDTO, usuarioLogado.getUsuarioId());
        return ResponseEntity.ok(frete);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletarFrete(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        freteService.deletarFrete(id, usuarioLogado.getUsuarioId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Frete deletado com sucesso");

        return ResponseEntity.ok(response);
    }

    /**
     * Método auxiliar para verificar permissão de visualização
     */
    private boolean verificarPermissaoVisualizacao(Frete frete, Usuario usuario) {
        if (usuario.getRole() == Usuario.Role.admin) {
            return true;
        }

        if (usuario.getRole() == Usuario.Role.motorista) {
            return frete.getStatus() == Frete.StatusFrete.anunciado ||
                    (frete.getMotorista() != null &&
                            frete.getMotorista().getUsuarioId().equals(usuario.getUsuarioId()));
        }

        if (usuario.getRole() == Usuario.Role.empresa) {
            return frete.getEmpresa().getUsuarioId().equals(usuario.getUsuarioId());
        }

        return false;
    }
}