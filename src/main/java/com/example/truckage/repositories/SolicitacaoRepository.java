package com.example.truckage.repositories;

import com.example.truckage.models.Frete;
import com.example.truckage.models.SolicitacaoFrete;
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
public interface SolicitacaoRepository extends JpaRepository<SolicitacaoFrete, Integer> {

    List<SolicitacaoFrete> findByEmpresaOrderByDataSolicitacaoDesc(Usuario empresa);

    List<SolicitacaoFrete> findByMotoristaOrderByDataSolicitacaoDesc(Usuario motorista);

    Optional<SolicitacaoFrete> findByFreteAndMotorista(Frete frete, Usuario motorista);

    boolean existsByFreteAndMotorista(Frete frete, Usuario motorista);

    @Modifying
    @Transactional
    @Query("UPDATE SolicitacaoFrete s SET s.status = 'rejeitada', s.dataResposta = :dataResposta " +
            "WHERE s.frete.freteId = :freteId AND s.solicitacaoId != :solicitacaoId " +
            "AND s.status = 'pendente'")
    int rejeitarOutrasSolicitacoes(@Param("freteId") Integer freteId,
                                   @Param("solicitacaoId") Integer solicitacaoId,
                                   @Param("dataResposta") LocalDateTime dataResposta);
}