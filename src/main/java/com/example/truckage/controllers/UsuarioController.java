package com.example.truckage.controllers;

import com.example.truckage.dtos.LoginDTO;
import com.example.truckage.dtos.UsuarioDTO;
import com.example.truckage.models.Usuario;
import com.example.truckage.security.JwtTokenProvider;
import com.example.truckage.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody UsuarioDTO dto) {
        try {
            System.out.println("=== RECEBENDO CADASTRO ===");
            System.out.println("Email: " + dto.email());
            System.out.println("Role: " + dto.role());
            System.out.println("Nome: " + dto.nomeCompleto());
            System.out.println("Celular: " + dto.celular());

            // Log para verificar se as imagens estão chegando
            if (dto.imagemBase64() != null) {
                System.out.println("Tem imagem de perfil: " + dto.imagemBase64().substring(0, 50) + "...");
            }
            if (dto.documentos() != null) {
                System.out.println("Documentos recebidos: " + dto.documentos().keySet());
            }

            Usuario usuario = usuarioService.criarUsuario(dto);
            usuario.setSenha(null);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuário cadastrado com sucesso");
            response.put("usuario", usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao cadastrar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> logar(@Valid @RequestBody LoginDTO dto) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(dto.email());

            if (!usuarioService.validarSenha(usuario, dto.senha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Senha incorreta"));
            }

            if (!usuario.getHabilitado()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Usuário desabilitado. Entre em contato com o administrador."));
            }

            String token = tokenProvider.generateToken(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("usuario", Map.of(
                    "usuario_id", usuario.getUsuarioId(),
                    "email", usuario.getEmail(),
                    "role", usuario.getRole(),
                    "nome_completo", usuario.getNomeCompleto(),
                    "imagem_perfil", usuario.getImagemPerfil(),
                    "habilitado", usuario.getHabilitado()
            ));
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro no login: " + e.getMessage()));
        }
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> listar(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuarioLogado = (Usuario) userDetails;
            return ResponseEntity.ok(usuarioService.listarUsuarios(usuarioLogado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao listar: " + e.getMessage()));
        }
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            usuario.setSenha(null);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao buscar: " + e.getMessage()));
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Integer id,
                                       @Valid @RequestBody UsuarioDTO dto,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuarioLogado = (Usuario) userDetails;
            Usuario usuario = usuarioService.atualizarUsuario(
                    id, dto, usuarioLogado.getUsuarioId(), usuarioLogado.getRole().name());
            usuario.setSenha(null);

            return ResponseEntity.ok(Map.of(
                    "message", "Usuário atualizado com sucesso",
                    "usuario", usuario
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao atualizar: " + e.getMessage()));
        }
    }

    @PatchMapping("/usuarios/{id}/alterar-senha")
    public ResponseEntity<?> alterarSenha(@PathVariable Integer id,
                                          @RequestBody Map<String, String> senhas,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuarioLogado = (Usuario) userDetails;

            usuarioService.alterarSenha(
                    id,
                    senhas.get("senhaAtual"),
                    senhas.get("novaSenha"),
                    usuarioLogado.getUsuarioId()
            );

            return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao alterar senha: " + e.getMessage()));
        }
    }

    @PatchMapping("/usuarios/{id}/documento")
    public ResponseEntity<?> atualizarDocumento(@PathVariable Integer id,
                                                @RequestBody Map<String, String> documento,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuarioLogado = (Usuario) userDetails;

            Usuario usuario = usuarioService.atualizarDocumento(
                    id,
                    documento.get("docType"),
                    documento.get("imageBase64"),
                    usuarioLogado.getUsuarioId()
            );
            usuario.setSenha(null);

            return ResponseEntity.ok(Map.of(
                    "message", "Documento atualizado com sucesso",
                    "usuario", usuario
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao atualizar documento: " + e.getMessage()));
        }
    }

    @PatchMapping("/usuarios/{id}/push-token")
    public ResponseEntity<?> atualizarPushToken(@PathVariable Integer id,
                                                @RequestBody Map<String, String> body,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuarioLogado = (Usuario) userDetails;

            usuarioService.atualizarPushToken(
                    id,
                    body.get("push_token"),
                    usuarioLogado.getUsuarioId()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Push token atualizado com sucesso",
                    "usuario_id", id
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao atualizar push token: " + e.getMessage()));
        }
    }
}