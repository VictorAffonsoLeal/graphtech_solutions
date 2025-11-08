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

import com.inter.graphtech_solutions.entities.ProdutoEntity;
import com.inter.graphtech_solutions.services.ProdutoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoEntity>> listar() {
        List<ProdutoEntity> lista = produtoService.listarProdutos();
        return ResponseEntity.ok().body(lista);
    }

    @PostMapping
    public ResponseEntity<ProdutoEntity> incluir(@RequestBody ProdutoEntity produto) {
        ProdutoEntity novo = produtoService.salvarProduto(produto);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoEntity> alterar(@PathVariable int id, @RequestBody ProdutoEntity produto) {
        ProdutoEntity atualizado = produtoService.alterarProduto(id, produto);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        produtoService.excluirProduto(id);;
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
