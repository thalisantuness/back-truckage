package com.example.truckage.controllers;

import com.example.truckage.models.SolicitacaoFrete;
import com.example.truckage.models.Usuario;
import com.example.truckage.services.SolicitacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    @PostMapping
    public ResponseEntity<SolicitacaoFrete> solicitar(
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        if (usuarioLogado.getRole() != Usuario.Role.motorista) {
            return ResponseEntity.status(403).build();
        }

        SolicitacaoFrete solicitacao = solicitacaoService.solicitar(
                body.get("frete_id"), usuarioLogado.getUsuarioId());

        return ResponseEntity.status(201).body(solicitacao);
    }

    @GetMapping("/empresa")
    public ResponseEntity<List<SolicitacaoFrete>> listarPorEmpresa(
            @AuthenticationPrincipal Usuario usuarioLogado) {

        if (usuarioLogado.getRole() != Usuario.Role.empresa) {
            return ResponseEntity.status(403).build();
        }

        List<SolicitacaoFrete> solicitacoes =
                solicitacaoService.listarPorEmpresa(usuarioLogado.getUsuarioId());

        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/motorista")
    public ResponseEntity<List<SolicitacaoFrete>> listarPorMotorista(
            @AuthenticationPrincipal Usuario usuarioLogado) {

        if (usuarioLogado.getRole() != Usuario.Role.motorista) {
            return ResponseEntity.status(403).build();
        }

        List<SolicitacaoFrete> solicitacoes =
                solicitacaoService.listarPorMotorista(usuarioLogado.getUsuarioId());

        return ResponseEntity.ok(solicitacoes);
    }

    @PutMapping("/{id}/responder")
    public ResponseEntity<SolicitacaoFrete> responder(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        if (usuarioLogado.getRole() != Usuario.Role.empresa) {
            return ResponseEntity.status(403).build();
        }

        SolicitacaoFrete.StatusSolicitacao status =
                SolicitacaoFrete.StatusSolicitacao.valueOf(body.get("status"));

        SolicitacaoFrete solicitacao =
                solicitacaoService.responder(id, status, usuarioLogado.getUsuarioId());

        return ResponseEntity.ok(solicitacao);
    }
}