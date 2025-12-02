package com.inter.graphtech_solutions.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        // Para novos pedidos, a lista vem do JSON e o Hibernate ainda não gerencia.
        // Mas precisamos garantir os vínculos antes de salvar.
        prepararItens(pedido); 
        return pedidoRepository.save(pedido);
    }

    public List<PedidoEntity> listarPedidos() {
        return pedidoRepository.findAllWithDetails();
    }

    public void deletarPedido(Integer id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional
    public PedidoEntity alterarPedido(Integer id, PedidoEntity pedidoInfoVindoDoFront) {
        Optional<PedidoEntity> pedidoExistenteOpt = pedidoRepository.findById(id);
        
        if (pedidoExistenteOpt.isPresent()) {
            PedidoEntity pedidoGerenciado = pedidoExistenteOpt.get();
            
            // 1. Atualiza campos simples
            pedidoGerenciado.setDescricao(pedidoInfoVindoDoFront.getDescricao());
            pedidoGerenciado.setDataCancel(pedidoInfoVindoDoFront.getDataCancel());
            pedidoGerenciado.setDataPedido(pedidoInfoVindoDoFront.getDataPedido());
            pedidoGerenciado.setCliente(pedidoInfoVindoDoFront.getCliente());
            pedidoGerenciado.setUsuario(pedidoInfoVindoDoFront.getUsuario());
            
            // 2. LÓGICA CRÍTICA PARA COLEÇÃO COM ORPHAN REMOVAL
            // Não podemos fazer pedidoGerenciado.setItens(novaLista)! Isso causa o erro.
            // Temos que manipular a lista existente.
            
            // A. Limpa a lista atual (remove os itens antigos do banco graças ao orphanRemoval=true)
            pedidoGerenciado.getItens().clear();
            
            // B. Se vieram novos itens, adiciona na lista existente
            if (pedidoInfoVindoDoFront.getItens() != null) {
                // Precisamos preparar esses novos itens (vincular IDs, produtos, etc) ANTES de adicionar
                // Para não bagunçar a referência 'pedidoInfoVindoDoFront', criamos uma lista auxiliar
                List<PedidoProdutoEntity> novosItensPreparados = new ArrayList<>();
                
                for (PedidoProdutoEntity item : pedidoInfoVindoDoFront.getItens()) {
                    if (item.getProduto() != null && item.getProduto().getIdProduto() != null) {
                        ProdutoEntity produtoReal = produtoRepository.findById(item.getProduto().getIdProduto()).orElse(null);
                        if (produtoReal != null) {
                            // Cria uma nova instância para garantir que é um novo objeto gerenciado
                            PedidoProdutoEntity novoItem = new PedidoProdutoEntity();
                            novoItem.setProduto(produtoReal);
                            novoItem.setPedido(pedidoGerenciado); // Vincula ao pedido gerenciado (Pai)
                            
                            novoItem.setValorUnitario(item.getValorUnitario() != null ? item.getValorUnitario() : produtoReal.getValor());
                            // CORREÇÃO: Usar a quantidade que vem do front (item.getQuantidade())
                            // Se vier nulo ou zero, usa 1 como padrão.
                            novoItem.setQtd((item.getQtd() != null && item.getQtd() > 0) ? item.getQtd() : 1);
                            
                            novoItem.setId(new PedidoProdutoEntity.PedidoProdutoId());
                            // Configura ID composto (mesmo que o save resolva, é bom garantir)
                            novoItem.getId().setPedidoId(pedidoGerenciado.getIdPedido());
                            novoItem.getId().setProdutoId(produtoReal.getIdProduto());
                            
                            novosItensPreparados.add(novoItem);
                        }
                    }
                }
                
                // C. Adiciona tudo na coleção gerenciada
                pedidoGerenciado.getItens().addAll(novosItensPreparados);
            }
            
            // O save() não é estritamente necessário dentro de @Transactional se a entidade está gerenciada,
            // mas o chamamos para garantir o retorno atualizado e triggers do ciclo de vida.
            return pedidoRepository.save(pedidoGerenciado);
        } else {
            return null;
        }
    }

    // Método auxiliar para preparar itens de um pedido NOVO (não gerenciado ainda)
    private void prepararItens(PedidoEntity pedido) {
        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            List<PedidoProdutoEntity> itensValidos = new ArrayList<>();

            for (PedidoProdutoEntity item : pedido.getItens()) {
                if (item.getProduto() != null && item.getProduto().getIdProduto() != null) {
                    ProdutoEntity produtoReal = produtoRepository.findById(item.getProduto().getIdProduto())
                        .orElse(null);
                    
                    if (produtoReal != null) {
                        item.setProduto(produtoReal);
                        item.setPedido(pedido); 
                        
                        if (item.getValorUnitario() == null) item.setValorUnitario(produtoReal.getValor());
                        // CORREÇÃO: Garantir que a quantidade venha do objeto item, senão default 1
                        item.setQtd((item.getQtd() != null && item.getQtd() > 0) ? item.getQtd() : 1);
                        
                        if (item.getId() == null) {
                            item.setId(new PedidoProdutoEntity.PedidoProdutoId());
                        }
                        if (pedido.getIdPedido() != null) {
                            item.getId().setPedidoId(pedido.getIdPedido());
                        }
                        item.getId().setProdutoId(produtoReal.getIdProduto());

                        itensValidos.add(item);
                    }
                }
            }
            // Aqui podemos usar setItens porque o objeto 'pedido' ainda não é gerenciado pelo Hibernate no contexto de 'salvar'
            pedido.setItens(itensValidos);
        }
    }

    @Transactional
    public PedidoEntity criarPedidoDeOrcamento(Integer orcamentoId) {
        OrcamentoEntity orcamento = orcamentoRepository.findById(orcamentoId)
            .orElseThrow(() -> new RuntimeException("Orçamento com ID " + orcamentoId + " não encontrado."));

        if (Boolean.FALSE.equals(orcamento.getStatus())) {
             throw new RuntimeException("Este orçamento não está aprovado.");
        }

        PedidoEntity novoPedido = new PedidoEntity();
        novoPedido.setDescricao("Pedido gerado do Orçamento #" + orcamento.getIdOrcamento() + ": " + orcamento.getDescricao());
        novoPedido.setDataPedido(LocalDate.now());
        novoPedido.setCliente(orcamento.getCliente());
        novoPedido.setUsuario(orcamento.getUsuario());
        novoPedido.setOrcamento(orcamento);
        
        if (orcamento.getItens() != null) {
            for (OrcamentoProdutoEntity itemOrcamento : orcamento.getItens()) {
                PedidoProdutoEntity itemPedido = new PedidoProdutoEntity();
                itemPedido.setProduto(itemOrcamento.getProduto());
                itemPedido.setPedido(novoPedido);
                // CORREÇÃO: Copiar a quantidade do orçamento para o pedido
                itemPedido.setQtd(itemOrcamento.getQtd());
                itemPedido.setValorUnitario(itemOrcamento.getValorUnitario());
                
                // ID composto
                itemPedido.setId(new PedidoProdutoEntity.PedidoProdutoId());
                itemPedido.getId().setProdutoId(itemOrcamento.getProduto().getIdProduto());
                
                novoPedido.getItens().add(itemPedido);
            }
        }

        return pedidoRepository.save(novoPedido);
    }
}