package com.inter.graphtech_solutions.controllers;
import com.inter.graphtech_solutions.entities.OrcamentoEntity;
import com.inter.graphtech_solutions.services.OrcamentoService;

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

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orcamentos")
public class OrcamentoController {
    private final OrcamentoService orcamentoService;

    @GetMapping
    public ResponseEntity<List<OrcamentoEntity>> listar() {
        List<OrcamentoEntity> lista = orcamentoService.listarOrcamento();
        return ResponseEntity.ok().body(lista);
    }

    @PostMapping
    public ResponseEntity<OrcamentoEntity> incluir(@RequestBody OrcamentoEntity Orcamento) {
        OrcamentoEntity novo = orcamentoService.salvar(Orcamento);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrcamentoEntity> alterar(@PathVariable int id, @RequestBody OrcamentoEntity Orcamento) {
        OrcamentoEntity atualizado = orcamentoService.alterar(id, Orcamento);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        orcamentoService.excluir(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
