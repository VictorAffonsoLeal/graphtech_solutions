package com.inter.graphtech_solutions.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.OrcamentoEntity;
import com.inter.graphtech_solutions.entities.OrcamentoProdutoEntity;
import com.inter.graphtech_solutions.entities.ProdutoEntity;
import com.inter.graphtech_solutions.repositories.OrcamentoRepository;
import com.inter.graphtech_solutions.repositories.ProdutoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrcamentoService {
    
    private final OrcamentoRepository orcamentoRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional
    public OrcamentoEntity salvar(OrcamentoEntity orcamento) {
        prepararItens(orcamento);
        return orcamentoRepository.save(orcamento);
    }

    public void excluir(int id){
        orcamentoRepository.deleteById(id);
    }

    public List<OrcamentoEntity> listarOrcamento(){
        return orcamentoRepository.findAllWithDetails();
    }

    @Transactional
    public OrcamentoEntity alterar(Integer id, OrcamentoEntity orcamentoInfo) {
        Optional<OrcamentoEntity> orcamentoExistenteOpt = orcamentoRepository.findById(id);
        
        if (orcamentoExistenteOpt.isPresent()) {
            OrcamentoEntity orcamentoGerenciado = orcamentoExistenteOpt.get();
            
            // 1. Atualiza campos simples
            orcamentoGerenciado.setDescricao(orcamentoInfo.getDescricao());
            orcamentoGerenciado.setDataCancel(orcamentoInfo.getDataCancel());
            orcamentoGerenciado.setDataOrcamento(orcamentoInfo.getDataOrcamento());
            orcamentoGerenciado.setStatus(orcamentoInfo.getStatus());
            orcamentoGerenciado.setCliente(orcamentoInfo.getCliente());
            orcamentoGerenciado.setUsuario(orcamentoInfo.getUsuario());
            
            // 2. LÓGICA CRÍTICA PARA COLEÇÃO (Evita NonUniqueObjectException)
            // A. Limpa a lista atual gerenciada
            orcamentoGerenciado.getItens().clear();
            
            // B. Se vieram novos itens, prepara e adiciona na lista existente
            if (orcamentoInfo.getItens() != null) {
                List<OrcamentoProdutoEntity> novosItensPreparados = new ArrayList<>();
                
                for (OrcamentoProdutoEntity item : orcamentoInfo.getItens()) {
                    if (item.getProduto() != null && item.getProduto().getIdProduto() != null) {
                        ProdutoEntity produtoReal = produtoRepository.findById(item.getProduto().getIdProduto()).orElse(null);
                        
                        if (produtoReal != null) {
                            // Cria nova instância para garantir unicidade na sessão
                            OrcamentoProdutoEntity novoItem = new OrcamentoProdutoEntity();
                            novoItem.setProduto(produtoReal);
                            novoItem.setOrcamento(orcamentoGerenciado); // Vincula ao Pai
                            
                            novoItem.setValorUnitario(item.getValorUnitario() != null ? item.getValorUnitario() : produtoReal.getValor());
                            // Garante que a QTD venha do front (ou 1 se nulo)
                            novoItem.setQtd((item.getQtd() != null && item.getQtd() > 0) ? item.getQtd() : 1);
                            
                            novoItem.setId(new OrcamentoProdutoEntity.OrcamentoProdutoId());
                            // Configura ID composto
                            novoItem.getId().setOrcamentoId(orcamentoGerenciado.getIdOrcamento());
                            novoItem.getId().setProdutoId(produtoReal.getIdProduto());
                            
                            novosItensPreparados.add(novoItem);
                        }
                    }
                }
                // C. Adiciona tudo na coleção gerenciada
                orcamentoGerenciado.getItens().addAll(novosItensPreparados);
            }
            
            return orcamentoRepository.save(orcamentoGerenciado);
        } else {
            return null;
        }
    }

    // Método auxiliar para preparar itens de um orcamento NOVO
    private void prepararItens(OrcamentoEntity orcamento) {
        if (orcamento.getItens() != null && !orcamento.getItens().isEmpty()) {
            List<OrcamentoProdutoEntity> itensValidos = new ArrayList<>();

            for (OrcamentoProdutoEntity item : orcamento.getItens()) {
                if (item.getProduto() != null && item.getProduto().getIdProduto() != null) {
                    ProdutoEntity produtoReal = produtoRepository.findById(item.getProduto().getIdProduto())
                        .orElse(null);
                    
                    if (produtoReal != null) {
                        item.setProduto(produtoReal);
                        item.setOrcamento(orcamento);
                        
                        if (item.getValorUnitario() == null) item.setValorUnitario(produtoReal.getValor());
                        // Garante a QTD
                        item.setQtd((item.getQtd() != null && item.getQtd() > 0) ? item.getQtd() : 1);
                        
                        if (item.getId() == null) {
                            item.setId(new OrcamentoProdutoEntity.OrcamentoProdutoId());
                        }
                        if (orcamento.getIdOrcamento() != null) {
                            item.getId().setOrcamentoId(orcamento.getIdOrcamento());
                        }
                        item.getId().setProdutoId(produtoReal.getIdProduto());

                        itensValidos.add(item);
                    }
                }
            }
            orcamento.setItens(itensValidos);
        }
    }
}