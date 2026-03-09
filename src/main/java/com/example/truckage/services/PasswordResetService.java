package com.example.truckage.services;

import com.example.truckage.models.PasswordReset;
import com.example.truckage.repositories.PasswordResetRepository;
import com.example.truckage.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    @Transactional
    public PasswordReset criarTokenReset(String email) {
        // Excluir tokens antigos
        passwordResetRepository.deleteByEmail(email);

        // Criar novo token
        PasswordReset reset = new PasswordReset();
        reset.setEmail(email);
        reset.setToken(UUID.randomUUID().toString());
        reset.setExpiresAt(LocalDateTime.now().plusHours(1));
        reset.setUsed(false);

        return passwordResetRepository.save(reset);
    }

    public PasswordReset buscarTokenValido(String token) {
        return passwordResetRepository
                .findByTokenAndUsedFalseAndExpiresAtGreaterThan(token, LocalDateTime.now())
                .orElse(null);
    }

    @Transactional
    public void marcarTokenComoUsado(String token) {
        PasswordReset reset = buscarTokenValido(token);
        if (reset != null) {
            reset.setUsed(true);
            passwordResetRepository.save(reset);
        }
    }

    @Transactional
    public void invalidarTokensPorEmail(String email) {
        passwordResetRepository.invalidateByEmail(email);
    }

    @Transactional
    public void solicitarReset(String email, String nomeUsuario, String frontendUrl) {
        PasswordReset reset = criarTokenReset(email);

        String resetLink = frontendUrl + "?token=" + reset.getToken();

        emailService.enviarEmailRecuperacaoSenha(email, nomeUsuario, resetLink);
    }
}