package com.inter.graphtech_solutions.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.ProdutoEntity;

@Repository
public interface ProdutoRepository extends JpaRepository <ProdutoEntity, Integer>{

}
