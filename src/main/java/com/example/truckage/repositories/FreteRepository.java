package com.example.truckage.repositories;

import com.example.truckage.models.Frete;
import com.example.truckage.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FreteRepository extends JpaRepository<Frete, Integer>,
        JpaSpecificationExecutor<Frete> {

    List<Frete> findByEmpresaOrMotorista(Usuario empresa, Usuario motorista);

    @Query("SELECT f FROM Frete f WHERE f.status = 'anunciado' AND f.empresa != :usuario")
    List<Frete> findFretesDisponiveisParaMotorista(@Param("usuario") Usuario usuario);

    @Query("SELECT f FROM Frete f WHERE f.empresa = :empresa OR f.motorista = :motorista " +
            "ORDER BY f.dataCriacao DESC")
    List<Frete> findByEmpresaOrMotoristaOrderByDataCriacaoDesc(
            @Param("empresa") Usuario empresa, @Param("motorista") Usuario motorista);
}