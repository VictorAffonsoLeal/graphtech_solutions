package com.inter.graphtech_solutions.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.UsuarioEntity;
import com.inter.graphtech_solutions.projection.TaxaConversaoProjection;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
    
    Optional<UsuarioEntity> findByLogin(String login);
    
    List<UsuarioEntity> findByStatus(Integer status);

    // NOVO: Chamada para a View
    @Query(value = "SELECT * FROM vw_taxa_conversao_vendedores", nativeQuery = true)
    List<TaxaConversaoProjection> buscarTaxaConversao();

    // Procedure
    @Query(value = "EXEC SP_CADUSUARIO :nome, :dataNascimento, :login, :senha", nativeQuery = true)
    Integer cadastrarUsuarioViaProcedure(
        @Param("nome") String nome,
        @Param("dataNascimento") LocalDate dataNascimento,
        @Param("login") String login,
        @Param("senha") String senha
    );

}