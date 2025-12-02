package com.inter.graphtech_solutions.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.ProdutoEntity;

@Repository
public interface ProdutoRepository extends JpaRepository <ProdutoEntity, Integer>{

    @Query(value = 
        "SELECT p.descricao as produto, l.data as dataAlteracao, " +
        "l.precoantigo as precoAntigo, l.preconovo as precoNovo, l.usuario " +
        "FROM LOG_HISTORICOPRECO l " +
        "JOIN PRODUTOS p ON l.produto_id = p.id_produto " +
        "ORDER BY l.data DESC", nativeQuery = true)
    List<LogPrecoProjection> buscarLogPrecos();

}
