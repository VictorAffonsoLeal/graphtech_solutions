package com.inter.graphtech_solutions.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Novo
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inter.graphtech_solutions.projection.RelatorioPedidoUsuarioProjection;
import com.inter.graphtech_solutions.projection.TaxaConversaoProjection;
import com.inter.graphtech_solutions.repositories.PedidoRepository;
import com.inter.graphtech_solutions.repositories.UsuarioRepository;

import jakarta.persistence.EntityManager; // Novo
import jakarta.persistence.PersistenceContext; // Novo
import jakarta.transaction.Transactional; // Novo
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    
    @PersistenceContext
    private EntityManager entityManager; // Para rodar SQL nativo de backup

    // FUNCTION: Vendas por Mês
    @GetMapping("/vendas-usuario")
    public ResponseEntity<List<RelatorioPedidoUsuarioProjection>> getVendasUsuario(
            @RequestParam Integer idUsuario,
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        
        List<RelatorioPedidoUsuarioProjection> resultado = 
            pedidoRepository.buscarTotalPedidosUsuario(idUsuario, mes, ano);
            
        return ResponseEntity.ok(resultado);
    }

    // VIEW: Taxa de Conversão
    @GetMapping("/taxa-conversao")
    public ResponseEntity<List<TaxaConversaoProjection>> getTaxaConversao() {
        List<TaxaConversaoProjection> resultado = usuarioRepository.buscarTaxaConversao();
        return ResponseEntity.ok(resultado);
    }

    // --- BACKUP & RESTORE ---

    @PostMapping("/backup")
    @Transactional
    public ResponseEntity<String> realizarBackup() {
        try {
            // Caminho absoluto para garantir permissão (Ajuste conforme seu ambiente)
            // SQL Server precisa de permissão para escrever aqui.
            // Tente criar a pasta C:\Backups manualmente antes.
            String caminhoBackup = "J:\\Fatec\\Fatec\\Interdisciplinar\\graphtech_solutions\\src\\main\\resources\\backup\\interdb.bak"; 
            
            String sql = "BACKUP DATABASE interdb TO DISK = '" + caminhoBackup + "' WITH INIT";
            
            entityManager.createNativeQuery(sql).executeUpdate();
            
            return ResponseEntity.ok("Backup realizado com sucesso em: " + caminhoBackup);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao fazer backup: " + e.getMessage());
        }
    }

    @PostMapping("/restore")
    @Transactional
    public ResponseEntity<String> realizarRestore() {
        try {
            String caminhoBackup = "J:\\Fatec\\Fatec\\Interdisciplinar\\graphtech_solutions\\src\\main\\resources\\backup\\interdb.bak";
            
            // Para restaurar, precisamos desconectar outros usuários.
            // USE master força a saída do banco atual.
            // ALTER DATABASE ... SET SINGLE_USER WITH ROLLBACK IMMEDIATE mata conexões ativas.
            String sql = "USE interdb; " +
                         "ALTER DATABASE interdb SET SINGLE_USER WITH ROLLBACK IMMEDIATE; " +
                         "RESTORE DATABASE interdb FROM DISK = '" + caminhoBackup + "' WITH REPLACE; " +
                         "ALTER DATABASE interdb SET MULTI_USER;";
            
            entityManager.createNativeQuery(sql).executeUpdate();
            
            return ResponseEntity.ok("Banco de dados restaurado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao restaurar: " + e.getMessage());
        }
    }
}