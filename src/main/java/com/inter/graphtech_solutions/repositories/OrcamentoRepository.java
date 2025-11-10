package com.inter.graphtech_solutions.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.OrcamentoEntity;

@Repository
public interface OrcamentoRepository extends JpaRepository <OrcamentoEntity, Integer>{
    @Query("SELECT o FROM OrcamentoEntity o " +
           "JOIN FETCH o.cliente " +
           "JOIN FETCH o.usuario " +
           "LEFT JOIN FETCH o.produtos " +
           "LEFT JOIN FETCH o.pedido")
    List<OrcamentoEntity> findAllWithDetails();

}
