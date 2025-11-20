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
import com.inter.graphtech_solutions.entities.UsuarioEntity;
import com.inter.graphtech_solutions.services.UsuarioService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/usuarios")
public class UsuarioController {
    
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioEntity>> listar() {
        List<UsuarioEntity> lista = usuarioService.listarUsuarios();
        return ResponseEntity.ok().body(lista);
    }

    @PostMapping
    public ResponseEntity<UsuarioEntity> incluir(@RequestBody UsuarioEntity usuario) {
        UsuarioEntity novo = usuarioService.salvarUsuario(usuario);
        if (novo != null) {
            return new ResponseEntity<>(novo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> alterar(@PathVariable Integer id, @RequestBody UsuarioEntity usuario) {
        UsuarioEntity atualizado = usuarioService.alterarUsuario(id, usuario);
        if (atualizado != null) {
            return new ResponseEntity<>(atualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        usuarioService.deletarUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}