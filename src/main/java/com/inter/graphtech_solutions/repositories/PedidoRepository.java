package com.inter.graphtech_solutions.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.PedidoEntity;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Integer> {

    // Atualizado para buscar Clientes, Usuarios, Itens e os Produtos dentro dos itens
    @Query("SELECT p FROM PedidoEntity p " +
           "LEFT JOIN FETCH p.cliente " +
           "LEFT JOIN FETCH p.usuario " +
           "LEFT JOIN FETCH p.itens i " +
           "LEFT JOIN FETCH i.produto")
    List<PedidoEntity> findAllWithDetails();

}