package com.inter.graphtech_solutions.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import com.inter.graphtech_solutions.entities.PedidoEntity;
import com.inter.graphtech_solutions.services.PedidoService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoEntity>> listar() {
        List<PedidoEntity> lista = pedidoService.listarPedidos();
        return ResponseEntity.ok().body(lista);
    }

    @PostMapping
    public ResponseEntity<PedidoEntity> incluir(@RequestBody PedidoEntity pedido) {
        PedidoEntity novo = pedidoService.salvarPedido(pedido);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

        @PostMapping("/from-orcamento/{orcamentoId}")
    public ResponseEntity<?> criarDeOrcamento(@PathVariable int orcamentoId) {
        try {
            PedidoEntity novoPedido = pedidoService.criarPedidoDeOrcamento(orcamentoId);
            return new ResponseEntity<>(novoPedido, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Captura exceções personalizadas (Ex: Orçamento não encontrado ou já processado)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoEntity> alterar(@PathVariable int id, @RequestBody PedidoEntity pedido) {
        PedidoEntity atualizado = pedidoService.alterarPedido(id, pedido);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        pedidoService.deletarPedido(id);;
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
