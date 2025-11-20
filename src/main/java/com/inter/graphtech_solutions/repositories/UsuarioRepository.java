package com.inter.graphtech_solutions.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.UsuarioEntity;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
    
    Optional<UsuarioEntity> findByLogin(String login);
    
    // Busca apenas usuários onde o status é 0 (Ativo)
    List<UsuarioEntity> findByStatus(Integer status);

}