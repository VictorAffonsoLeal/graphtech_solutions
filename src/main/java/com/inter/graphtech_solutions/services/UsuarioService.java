package com.inter.graphtech_solutions.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.UsuarioEntity;
import com.inter.graphtech_solutions.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioEntity salvarUsuario(UsuarioEntity usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<UsuarioEntity> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public void deletarUsuario(int idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    public UsuarioEntity alterarUsuario(int id, UsuarioEntity usuario){
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            UsuarioEntity usuarioAtualizado = usuarioExistente.get();
            usuarioAtualizado.setNome(usuario.getNome());
            usuarioAtualizado.setLogin(usuario.getLogin());
            usuarioAtualizado.setSenha(usuario.getSenha());
            usuarioAtualizado.setDataNascimento(usuario.getDataNascimento());
            return usuarioRepository.save(usuarioAtualizado);
        } else {
            return null; // Ou lançar uma exceção apropriada
        }

    }

    public boolean validarLogin(String login, String senha) {
        Optional<UsuarioEntity> usuarioLogin = usuarioRepository.findByLogin(login);

        if (usuarioLogin.isEmpty()) {
            return false;
        }

        UsuarioEntity usuario = usuarioLogin.get();
        return usuario.getSenha().equals(senha);
    }

}
