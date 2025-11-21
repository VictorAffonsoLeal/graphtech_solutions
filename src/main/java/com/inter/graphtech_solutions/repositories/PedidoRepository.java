package com.inter.graphtech_solutions.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inter.graphtech_solutions.entities.PedidoEntity;
import com.inter.graphtech_solutions.projection.RelatorioPedidoUsuarioProjection;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Integer> {

    // Método para carregar pedidos completos (com itens e produtos)
    @Query("SELECT p FROM PedidoEntity p " +
           "LEFT JOIN FETCH p.cliente " +
           "LEFT JOIN FETCH p.usuario " +
           "LEFT JOIN FETCH p.itens i " +
           "LEFT JOIN FETCH i.produto")
    List<PedidoEntity> findAllWithDetails();

    // CORREÇÃO DO ERRO: Declarando o método que o Controller está tentando chamar
    @Query(value = "SELECT * FROM dbo.fc_qtdPedidoMesUsuario(:idUsuario, :mes, :ano)", nativeQuery = true)
    List<RelatorioPedidoUsuarioProjection> buscarTotalPedidosUsuario(
        @Param("idUsuario") Integer idUsuario, 
        @Param("mes") Integer mes, 
        @Param("ano") Integer ano
    );
}