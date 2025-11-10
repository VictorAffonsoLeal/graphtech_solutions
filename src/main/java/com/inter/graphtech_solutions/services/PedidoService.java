package com.inter.graphtech_solutions.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.OrcamentoEntity;
import com.inter.graphtech_solutions.entities.PedidoEntity;
import com.inter.graphtech_solutions.entities.ProdutoEntity;
import com.inter.graphtech_solutions.repositories.OrcamentoRepository;
import com.inter.graphtech_solutions.repositories.PedidoRepository;
import com.inter.graphtech_solutions.repositories.ProdutoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final OrcamentoRepository orcamentoRepository;

    @Transactional
    public PedidoEntity salvarPedido(PedidoEntity pedido) {
        vincularProdutosExistentes(pedido, new ArrayList<>(pedido.getProdutos()));
        return pedidoRepository.save(pedido);
    }

    public List<PedidoEntity> listarPedidos() {
        return pedidoRepository.findAllWithDetails();
    }

    public void deletarPedido(int id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional
    public PedidoEntity alterarPedido(int id, PedidoEntity pedido){
        Optional<PedidoEntity> pedidoExistente = pedidoRepository.findById(id);
        if (pedidoExistente.isPresent()) {
            PedidoEntity pedidoAtualizado = pedidoExistente.get();
            pedidoAtualizado.setDescricao(pedido.getDescricao());
            pedidoAtualizado.setDataCancel(pedido.getDataCancel());
            pedidoAtualizado.setDataPedido(pedido.getDataPedido());
            pedidoAtualizado.setCliente(pedido.getCliente());
            pedidoAtualizado.setUsuario(pedido.getUsuario());
            vincularProdutosExistentes(pedidoAtualizado, new ArrayList<>(pedidoAtualizado.getProdutos()));
            //pedidoAtualizado.setProdutos(pedido.getProdutos());
            return pedidoRepository.save(pedidoAtualizado);
        } else {
            return null; // ou lançar uma exceção
        }
    }

    private void vincularProdutosExistentes(PedidoEntity pedido, List<ProdutoEntity> produtos) {
        if (produtos != null && !produtos.isEmpty()) {
            // Busca os produtos REAIS do banco de dados pelos IDs
            // CORREÇÃO: Coletar para um Set
            Set<ProdutoEntity> produtosGerenciados = produtos.stream()
                .map(produto -> produtoRepository.findById(produto.getIdProduto()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            
            pedido.setProdutos(produtosGerenciados); // Define o Set de produtos gerenciados
        } else {
            pedido.setProdutos(new HashSet<>()); // Limpa a lista se ela vier vazia
        }
    }

    @Transactional
    public PedidoEntity criarPedidoDeOrcamento(int orcamentoId) {
        // 1. Encontra o orçamento
        OrcamentoEntity orcamento = orcamentoRepository.findById(orcamentoId)
            .orElseThrow(() -> new RuntimeException("Orçamento com ID " + orcamentoId + " não encontrado."));

        // 2. Verifica se já foi processado
        if (orcamento.getPedido() != null) {
            throw new RuntimeException("Este orçamento já foi processado no Pedido #" + orcamento.getPedido().getIdPedido());
        }

        // 3. Verifica se está aprovado (opcional, mas recomendado)
        if (!orcamento.isStatus()) {
             throw new RuntimeException("Este orçamento não está aprovado e não pode ser convertido em pedido.");
        }

        // 4. Cria o novo pedido
        PedidoEntity novoPedido = new PedidoEntity();
        novoPedido.setDescricao("Pedido gerado do Orçamento #" + orcamento.getIdOrcamento() + ": " + orcamento.getDescricao());
        novoPedido.setDataPedido(LocalDate.now());
        novoPedido.setCliente(orcamento.getCliente());
        novoPedido.setUsuario(orcamento.getUsuario());
        
        // 5. Copia os produtos do orçamento para o pedido
        novoPedido.setProdutos(new HashSet<>(orcamento.getProdutos()));
        
        // 6. Vincula o pedido ao orçamento (lado que "possui" a relação)
        novoPedido.setOrcamento(orcamento);

        // 7. Salva o novo pedido
        return pedidoRepository.save(novoPedido);
    }

}
