package com.inter.graphtech_solutions.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.ClienteEntity;

@Repository
public interface ClienteRepository extends JpaRepository <ClienteEntity, Integer>{
    @Query("SELECT c FROM ClienteEntity c JOIN FETCH c.usuario")
    List<ClienteEntity> findAllWithUsuario();

}
