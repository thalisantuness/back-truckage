package com.example.truckage.repositories;

import com.example.truckage.models.Conversa;
import com.example.truckage.models.Mensagem;
import com.example.truckage.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Integer> {

    List<Mensagem> findByConversaOrderByDataEnvioAsc(Conversa conversa);

    @Modifying
    @Transactional
    @Query("UPDATE Mensagem m SET m.lida = true WHERE m.mensagemId = :mensagemId " +
            "AND m.remetente.usuarioId != :usuarioId")
    int marcarComoLida(@Param("mensagemId") Integer mensagemId,
                       @Param("usuarioId") Integer usuarioId);

    long countByConversaAndLidaFalseAndRemetenteNot(Conversa conversa, Usuario remetente);
}