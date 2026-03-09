package com.example.truckage.repositories;

import com.example.truckage.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Buscar por email
    Optional<Usuario> findByEmail(String email);

    // Verificar se email existe
    boolean existsByEmail(String email);

    // Buscar por role (MÉTODO QUE ESTAVA FALTANDO)
    List<Usuario> findByRole(Usuario.Role role);

    // Buscar por token de reset de senha válido
    Optional<Usuario> findByPasswordResetTokenAndPasswordResetExpiresGreaterThan(
            String token, LocalDateTime now);

    // Atualizar push token
    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.pushToken = :pushToken WHERE u.usuarioId = :usuarioId")
    int updatePushToken(@Param("usuarioId") Integer usuarioId,
                        @Param("pushToken") String pushToken);

    // Atualizar token de reset de senha
    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.passwordResetToken = :token, " +
            "u.passwordResetExpires = :expires WHERE u.email = :email")
    void updateResetToken(@Param("email") String email,
                          @Param("token") String token,
                          @Param("expires") LocalDateTime expires);

    // Atualizar senha e limpar token de reset
    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.senha = :senha, " +
            "u.passwordResetToken = null, u.passwordResetExpires = null " +
            "WHERE u.usuarioId = :usuarioId")
    void updateSenhaAndClearResetToken(@Param("usuarioId") Integer usuarioId,
                                       @Param("senha") String senha);

    // Buscar usuários por role e habilitado
    List<Usuario> findByRoleAndHabilitado(Usuario.Role role, Boolean habilitado);

    // Buscar usuários com push token não nulo
    List<Usuario> findByPushTokenIsNotNull();

    // Buscar motoristas habilitados
    @Query("SELECT u FROM Usuario u WHERE u.role = 'motorista' AND u.habilitado = true")
    List<Usuario> findMotoristasHabilitados();

    // Buscar empresas habilitadas
    @Query("SELECT u FROM Usuario u WHERE u.role = 'empresa' AND u.habilitado = true")
    List<Usuario> findEmpresasHabilitadas();

    // Contar usuários por role
    long countByRole(Usuario.Role role);

    // Buscar por nome (case insensitive)
    List<Usuario> findByNomeCompletoContainingIgnoreCase(String nome);

    // Buscar por celular
    Optional<Usuario> findByCelular(String celular);
}