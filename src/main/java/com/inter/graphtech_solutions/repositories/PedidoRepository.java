package com.inter.graphtech_solutions.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.PedidoEntity;

@Repository
public interface PedidoRepository extends JpaRepository <PedidoEntity, Integer>{
    @Query("SELECT p FROM PedidoEntity p JOIN FETCH p.cliente JOIN FETCH p.usuario")
    List<PedidoEntity> findAllWithDetails();

}
