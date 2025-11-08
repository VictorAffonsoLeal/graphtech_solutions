package com.inter.graphtech_solutions.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inter.graphtech_solutions.entities.ClienteEntity;
import com.inter.graphtech_solutions.services.ClienteService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteEntity>> listar(){
        List<ClienteEntity> clientes = clienteService.listarClientes();
        return ResponseEntity.ok().body(clientes);
    }

    @PostMapping
    public ResponseEntity<ClienteEntity> incluir(@RequestBody ClienteEntity cliente) {
        ClienteEntity novo = clienteService.salvarCliente(cliente);
        if (novo != null){
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteEntity> alterar(@PathVariable int id, @RequestBody ClienteEntity cliente) {
        ClienteEntity atualizado = clienteService.alterarCliente(id, cliente);
        if (atualizado != null){
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        clienteService.deletarCliente(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
    


