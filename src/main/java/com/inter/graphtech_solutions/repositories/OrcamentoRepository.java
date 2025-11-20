package com.inter.graphtech_solutions.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.OrcamentoEntity;

@Repository
public interface OrcamentoRepository extends JpaRepository<OrcamentoEntity, Integer> {
    
    // Atualizado para buscar Clientes, Usuarios, Itens e os Produtos dentro dos itens
    @Query("SELECT o FROM OrcamentoEntity o " +
           "LEFT JOIN FETCH o.cliente " +
           "LEFT JOIN FETCH o.usuario " +
           "LEFT JOIN FETCH o.itens i " +
           "LEFT JOIN FETCH i.produto")
    List<OrcamentoEntity> findAllWithDetails();
}