package com.inter.graphtech_solutions.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.UsuarioEntity;
import com.inter.graphtech_solutions.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioEntity salvarUsuario(UsuarioEntity usuario) {
        // Chama a procedure
        Integer novoId = usuarioRepository.cadastrarUsuarioViaProcedure(
            usuario.getNome(),
            usuario.getDataNascimento(),
            usuario.getLogin(),
            usuario.getSenha()
        );

        // Busca o registro criado
        return usuarioRepository.findById(novoId).orElse(null);
    }

    public List<UsuarioEntity> listarUsuarios() {
        // Retorna apenas os ativos para a tela
        return usuarioRepository.findByStatus(0);
    }

    public void deletarUsuario(Integer idUsuario) {
        // SOFT DELETE: Não apaga do banco. Apenas marca como inativo (1).
        Optional<UsuarioEntity> usuario = usuarioRepository.findById(idUsuario);
        if (usuario.isPresent()) {
            UsuarioEntity u = usuario.get();
            u.setStatus(1); // Marca como deletado
            usuarioRepository.save(u);
        }
    }

    public UsuarioEntity alterarUsuario(Integer id, UsuarioEntity usuario){
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);
        
        if (usuarioExistente.isPresent()) {
            UsuarioEntity usuarioAtualizado = usuarioExistente.get();
            
            usuarioAtualizado.setNome(usuario.getNome());
            usuarioAtualizado.setDataNascimento(usuario.getDataNascimento());
            usuarioAtualizado.setLogin(usuario.getLogin());
            usuarioAtualizado.setSenha(usuario.getSenha());
            // Mantém o status original ou atualiza se necessário
            
            return usuarioRepository.save(usuarioAtualizado);
        } else {
            return null;
        }
    }

    public boolean validarLogin(String login, String senha) {
        Optional<UsuarioEntity> usuarioLogin = usuarioRepository.findByLogin(login);

        // Verifica se existe, se a senha bate E SE O USUÁRIO ESTÁ ATIVO (status 0)
        if (usuarioLogin.isEmpty() || usuarioLogin.get().getStatus() != 0) {
            return false;
        }

        UsuarioEntity usuario = usuarioLogin.get();
        return usuario.getSenha().equals(senha);
    }
}