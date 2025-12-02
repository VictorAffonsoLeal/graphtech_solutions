package com.inter.graphtech_solutions.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.ClienteEntity;
import com.inter.graphtech_solutions.repositories.ClienteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteEntity salvarCliente(ClienteEntity cliente) {
        // Valida se tem usuário vinculado
        if (cliente.getUsuario() == null || cliente.getUsuario().getIdPessoa() == null) {
            throw new RuntimeException("Usuário responsável é obrigatório.");
        }

        // Chama a procedure no banco e recebe o ID novo
        Integer novoId = clienteRepository.cadastrarClienteViaProcedure(
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getEndereco(),
            cliente.getTelefone(),
            cliente.getDataNascimento(),
            cliente.getUsuario().getIdPessoa()
        );

        // Busca o objeto completo criado para retornar ao controller
        return clienteRepository.findById(novoId).orElse(null);
    }

    public ClienteEntity alterarCliente(Integer id, ClienteEntity cliente) {
        Optional<ClienteEntity> clienteExistente = clienteRepository.findById(id);
        
        if (clienteExistente.isPresent()) {
            ClienteEntity clienteAtualizado = clienteExistente.get();
            
            clienteAtualizado.setNome(cliente.getNome());
            clienteAtualizado.setDataNascimento(cliente.getDataNascimento());
            clienteAtualizado.setTelefone(cliente.getTelefone());
            clienteAtualizado.setEmail(cliente.getEmail());
            clienteAtualizado.setDataCadastro(cliente.getDataCadastro());
            clienteAtualizado.setEndereco(cliente.getEndereco());
            clienteAtualizado.setUsuario(cliente.getUsuario());
            
            return clienteRepository.save(clienteAtualizado);
        } else {
            return null;
        }
    }

    public void deletarCliente(Integer id) {
        // SOFT DELETE TAMBÉM PARA CLIENTES
        Optional<ClienteEntity> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            ClienteEntity c = cliente.get();
            c.setStatus(1);
            clienteRepository.save(c);
        }
    }

    public List<ClienteEntity> listarClientes() {
        // O Repositório já filtra WHERE status = 0
        return clienteRepository.findAllWithUsuario();
    }
}