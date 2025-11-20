package com.inter.graphtech_solutions.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.ClienteEntity;
import com.inter.graphtech_solutions.repositories.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepository clienteRepository;

    public ClienteEntity salvarCliente(ClienteEntity cliente) {
        if (cliente.getStatus() == null) {
            cliente.setStatus(0);
        }
        return clienteRepository.save(cliente);
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