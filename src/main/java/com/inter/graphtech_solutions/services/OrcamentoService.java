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
            
            orcamentoGerenciado.setDescricao(orcamentoInfo.getDescricao());
            orcamentoGerenciado.setDataCancel(orcamentoInfo.getDataCancel());
            orcamentoGerenciado.setDataOrcamento(orcamentoInfo.getDataOrcamento());
            orcamentoGerenciado.setStatus(orcamentoInfo.getStatus());
            orcamentoGerenciado.setCliente(orcamentoInfo.getCliente());
            orcamentoGerenciado.setUsuario(orcamentoInfo.getUsuario());
            
            orcamentoGerenciado.getItens().clear();
            
            if (orcamentoInfo.getItens() != null) {
                List<OrcamentoProdutoEntity> novosItensPreparados = new ArrayList<>();
                
                for (OrcamentoProdutoEntity item : orcamentoInfo.getItens()) {
                    if (item.getProduto() != null && item.getProduto().getIdProduto() != null) {
                        ProdutoEntity produtoReal = produtoRepository.findById(item.getProduto().getIdProduto()).orElse(null);
                        
                        if (produtoReal != null) {

                            OrcamentoProdutoEntity novoItem = new OrcamentoProdutoEntity();
                            novoItem.setProduto(produtoReal);
                            novoItem.setOrcamento(orcamentoGerenciado);
                            
                            novoItem.setValorUnitario(item.getValorUnitario() != null ? item.getValorUnitario() : produtoReal.getValor());
                            novoItem.setQtd((item.getQtd() != null && item.getQtd() > 0) ? item.getQtd() : 1);
                            
                            novoItem.setId(new OrcamentoProdutoEntity.OrcamentoProdutoId());
                            novoItem.getId().setOrcamentoId(orcamentoGerenciado.getIdOrcamento());
                            novoItem.getId().setProdutoId(produtoReal.getIdProduto());
                            
                            novosItensPreparados.add(novoItem);
                        }
                    }
                }
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