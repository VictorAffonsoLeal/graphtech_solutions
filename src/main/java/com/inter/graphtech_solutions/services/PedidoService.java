package com.inter.graphtech_solutions.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.OrcamentoEntity;
import com.inter.graphtech_solutions.entities.OrcamentoProdutoEntity;
import com.inter.graphtech_solutions.entities.PedidoEntity;
import com.inter.graphtech_solutions.entities.PedidoProdutoEntity;
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
        prepararItens(pedido);
        return pedidoRepository.save(pedido);
    }

    public List<PedidoEntity> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public void deletarPedido(Integer id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional
    public PedidoEntity alterarPedido(Integer id, PedidoEntity pedido) {
        Optional<PedidoEntity> pedidoExistenteOpt = pedidoRepository.findById(id);
        
        if (pedidoExistenteOpt.isPresent()) {
            PedidoEntity pedidoAtualizado = pedidoExistenteOpt.get();
            
            pedidoAtualizado.setDescricao(pedido.getDescricao());
            pedidoAtualizado.setDataCancel(pedido.getDataCancel());
            pedidoAtualizado.setDataPedido(pedido.getDataPedido());
            pedidoAtualizado.setCliente(pedido.getCliente());
            pedidoAtualizado.setUsuario(pedido.getUsuario());
            
            // Atualiza lista de itens
            pedidoAtualizado.getItens().clear();
            if (pedido.getItens() != null) {
                pedidoAtualizado.getItens().addAll(pedido.getItens());
            }
            
            prepararItens(pedidoAtualizado);
            
            return pedidoRepository.save(pedidoAtualizado);
        } else {
            return null;
        }
    }

    // Método auxiliar para vincular Produtos e o Próprio Pedido aos itens
    private void prepararItens(PedidoEntity pedido) {
        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            List<PedidoProdutoEntity> itensParaRemover = new ArrayList<>();

            for (PedidoProdutoEntity item : pedido.getItens()) {
                if (item.getProduto() != null && item.getProduto().getIdProduto() != null) {
                    // Busca o produto para garantir que temos o objeto completo (valor atual, etc)
                    ProdutoEntity produtoReal = produtoRepository.findById(item.getProduto().getIdProduto())
                        .orElse(null);
                    
                    if (produtoReal != null) {
                        item.setProduto(produtoReal);
                        item.setPedido(pedido); // Vincula o pai (Pedido) ao filho (Item)
                        
                        // Lógica de negócio: Se o valor unitário não veio do front, usa o do cadastro
                        if (item.getValorUnitario() == null) {
                            item.setValorUnitario(produtoReal.getValor());
                        }
                        // Se quantidade não veio, assume 1
                        if (item.getQtd() == null) {
                            item.setQtd(1);
                        }
                        
                    } else {
                        itensParaRemover.add(item);
                    }
                } else {
                    itensParaRemover.add(item);
                }
            }
            pedido.getItens().removeAll(itensParaRemover);
        }
    }

    @Transactional
    public PedidoEntity criarPedidoDeOrcamento(Integer orcamentoId) {
        OrcamentoEntity orcamento = orcamentoRepository.findById(orcamentoId)
            .orElseThrow(() -> new RuntimeException("Orçamento com ID " + orcamentoId + " não encontrado."));

        // Validações...
        if (Boolean.FALSE.equals(orcamento.getStatus())) {
             throw new RuntimeException("Este orçamento não está aprovado.");
        }

        PedidoEntity novoPedido = new PedidoEntity();
        novoPedido.setDescricao("Pedido gerado do Orçamento #" + orcamento.getIdOrcamento() + ": " + orcamento.getDescricao());
        novoPedido.setDataPedido(LocalDate.now());
        novoPedido.setCliente(orcamento.getCliente());
        novoPedido.setUsuario(orcamento.getUsuario());
        novoPedido.setOrcamento(orcamento);
        
        // Converte Itens de Orçamento -> Itens de Pedido
        if (orcamento.getItens() != null) {
            for (OrcamentoProdutoEntity itemOrcamento : orcamento.getItens()) {
                PedidoProdutoEntity itemPedido = new PedidoProdutoEntity();
                
                itemPedido.setProduto(itemOrcamento.getProduto());
                itemPedido.setPedido(novoPedido);
                
                // COPIA AQUI OS DADOS DO ITEM
                itemPedido.setQtd(itemOrcamento.getQtd());
                itemPedido.setValorUnitario(itemOrcamento.getValorUnitario());
                
                novoPedido.getItens().add(itemPedido);
            }
        }

        return pedidoRepository.save(novoPedido);
    }
}