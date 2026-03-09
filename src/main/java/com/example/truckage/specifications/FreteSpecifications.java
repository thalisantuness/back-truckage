package com.example.truckage.specifications;

import com.example.truckage.models.Frete;
import com.example.truckage.models.Usuario;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;

public class FreteSpecifications {

    /**
     * Filtro para fretes com status ANUNCIADO
     */
    public static Specification<Frete> statusAnunciado() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), Frete.StatusFrete.anunciado);
    }

    /**
     * Filtro para fretes que NÃO são da empresa do usuário
     */
    public static Specification<Frete> empresaDiferente(Usuario usuario) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("empresa"), usuario);
    }

    /**
     * Filtro para fretes que pertencem ao usuário (como empresa OU motorista)
     */
    public static Specification<Frete> pertenceAoUsuario(Usuario usuario) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("empresa"), usuario),
                        criteriaBuilder.equal(root.get("motorista"), usuario)
                );
    }

    /**
     * Filtro para origem da cidade (case insensitive)
     */
    public static Specification<Frete> origemCidadeLike(String cidade) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("origemCidade")),
                        "%" + cidade.toLowerCase() + "%"
                );
    }

    /**
     * Filtro para destino da cidade (case insensitive)
     */
    public static Specification<Frete> destinoCidadeLike(String cidade) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("destinoCidade")),
                        "%" + cidade.toLowerCase() + "%"
                );
    }

    /**
     * Filtro para tipo de carga (case insensitive)
     */
    public static Specification<Frete> tipoCargaLike(String tipoCarga) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("tipoCarga")),
                        "%" + tipoCarga.toLowerCase() + "%"
                );
    }

    /**
     * Filtro para valor do frete maior ou igual ao informado
     */
    public static Specification<Frete> valorFreteMaiorQue(BigDecimal valor) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("valorFrete"), valor);
    }

    /**
     * Filtro para veículo de tração (case insensitive)
     */
    public static Specification<Frete> veiculoTracaoLike(String veiculoTracao) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("veiculoTracao")),
                        "%" + veiculoTracao.toLowerCase() + "%"
                );
    }

    /**
     * Filtro para tipos de carreta (case insensitive)
     */
    public static Specification<Frete> tiposCarretaLike(String tiposCarreta) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("tiposCarreta")),
                        "%" + tiposCarreta.toLowerCase() + "%"
                );
    }

    /**
     * Filtro para data prevista de entrega
     */
    public static Specification<Frete> dataPrevistaEntregaGreaterThanOrEqual(java.time.LocalDateTime data) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("dataPrevistaEntrega"), data);
    }

    /**
     * Filtro para precisa de lona
     */
    public static Specification<Frete> precisaLona(boolean precisa) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("precisaLona"), precisa);
    }

    /**
     * Filtro para produto químico
     */
    public static Specification<Frete> produtoQuimico(boolean quimico) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("produtoQuimico"), quimico);
    }
}