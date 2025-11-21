package com.inter.graphtech_solutions.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inter.graphtech_solutions.projection.RelatorioPedidoUsuarioProjection;
import com.inter.graphtech_solutions.projection.TaxaConversaoProjection;
import com.inter.graphtech_solutions.repositories.PedidoRepository;
import com.inter.graphtech_solutions.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository; // Injetar UsuarioRepository

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

    // VIEW: Taxa de Conversão (Novo)
    @GetMapping("/taxa-conversao")
    public ResponseEntity<List<TaxaConversaoProjection>> getTaxaConversao() {
        List<TaxaConversaoProjection> resultado = usuarioRepository.buscarTaxaConversao();
        return ResponseEntity.ok(resultado);
    }
}