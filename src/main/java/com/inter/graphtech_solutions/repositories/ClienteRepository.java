package com.inter.graphtech_solutions.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.ClienteEntity;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Integer> {
    
    // Atualizado: Traz apenas clientes com status 0 (Ativos)
    @Query("SELECT c FROM ClienteEntity c JOIN FETCH c.usuario WHERE c.status = 0")
    List<ClienteEntity> findAllWithUsuario();

    // Chamada Nativa da Procedure
    // nativeQuery = true permite usar SQL puro (EXEC ...)
    // Retorna o ID gerado pela procedure
    @Query(value = "EXEC SP_CADCLIENTE :nome, :email, :endereco, :telefone, :dataNascimento, :usuarioId", nativeQuery = true)
    Integer cadastrarClienteViaProcedure(
        @Param("nome") String nome,
        @Param("email") String email,
        @Param("endereco") String endereco,
        @Param("telefone") String telefone,
        @Param("dataNascimento") LocalDate dataNascimento,
        @Param("usuarioId") Integer usuarioId
    );

}