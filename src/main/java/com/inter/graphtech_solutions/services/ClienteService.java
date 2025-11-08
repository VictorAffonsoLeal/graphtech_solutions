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
        return clienteRepository.save(cliente);
    }

    public ClienteEntity alterarCliente(int id, ClienteEntity cliente) {
        Optional<ClienteEntity> clienteExistente = clienteRepository.findById(id);
        if (clienteExistente.isPresent()) {
            ClienteEntity clienteAtualizado = clienteExistente.get();
            clienteAtualizado.setNome(cliente.getNome());
            clienteAtualizado.setTelefone(cliente.getTelefone());
            clienteAtualizado.setEmail(cliente.getEmail());
            clienteAtualizado.setDataNascimento(cliente.getDataNascimento());
            clienteAtualizado.setDataCadastro(cliente.getDataCadastro());
            clienteAtualizado.setEndereco(cliente.getEndereco());
            clienteAtualizado.setUsuario(cliente.getUsuario());
            
            return clienteRepository.save(clienteAtualizado);
        } else {
            return null;
        }
    }

    public void deletarCliente(int id) {
        clienteRepository.deleteById(id);
    }

    public List<ClienteEntity> listarClientes() {
        return clienteRepository.findAllWithUsuario();
    }

}
