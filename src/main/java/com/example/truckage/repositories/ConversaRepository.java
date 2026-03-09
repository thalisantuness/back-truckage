package com.example.truckage.repositories;

import com.example.truckage.models.Conversa;
import com.example.truckage.models.Frete;
import com.example.truckage.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversaRepository extends JpaRepository<Conversa, Integer> {

    List<Conversa> findByUsuario1OrUsuario2OrderByUltimaMensagemDesc(
            Usuario usuario1, Usuario usuario2);

    Optional<Conversa> findByFrete(Frete frete);

    boolean existsByFreteAndUsuario1AndUsuario2(Frete frete, Usuario usuario1, Usuario usuario2);

    @Query("SELECT c FROM Conversa c WHERE c.frete.freteId = :freteId")
    Optional<Conversa> findByFreteId(@Param("freteId") Integer freteId);
}