package com.inter.graphtech_solutions.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.PedidoEntity;
import com.inter.graphtech_solutions.repositories.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;

    public PedidoEntity salvarPedido(PedidoEntity pedido) {
        return pedidoRepository.save(pedido);
    }

    public List<PedidoEntity> listarPedidos() {
        return pedidoRepository.findAllWithDetails();
    }

    public void deletarPedido(int id) {
        pedidoRepository.deleteById(id);
    }

    public PedidoEntity alterarPedido(int id, PedidoEntity pedido){
        Optional<PedidoEntity> pedidoExistente = pedidoRepository.findById(id);
        if (pedidoExistente.isPresent()) {
            PedidoEntity pedidoAtualizado = pedidoExistente.get();
            pedidoAtualizado.setDescricao(pedido.getDescricao());
            pedidoAtualizado.setDataCancel(pedido.getDataCancel());
            pedidoAtualizado.setDataPedido(pedido.getDataPedido());
            pedidoAtualizado.setCliente(pedido.getCliente());
            pedidoAtualizado.setUsuario(pedido.getUsuario());
            //pedidoAtualizado.setProdutos(pedido.getProdutos());
            return pedidoRepository.save(pedidoAtualizado);
        } else {
            return null; // ou lançar uma exceção
        }
    }

}
