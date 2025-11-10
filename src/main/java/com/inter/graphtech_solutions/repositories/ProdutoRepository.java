package com.inter.graphtech_solutions.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.ProdutoEntity;

@Repository
public interface ProdutoRepository extends JpaRepository <ProdutoEntity, Integer>{
    @Query("SELECT p FROM ProdutoEntity p LEFT JOIN FETCH p.pedidos LEFT JOIN FETCH p.orcamentos")
    List<ProdutoEntity> findAllWithDetails();

}
