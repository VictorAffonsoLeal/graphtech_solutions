package com.inter.graphtech_solutions.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        // Prepara os itens antes de salvar
        prepararItens(orcamento);
        return orcamentoRepository.save(orcamento);
    }

    public void excluir(int id){
        orcamentoRepository.deleteById(id);
    }

    public List<OrcamentoEntity> listarOrcamento(){
        // Certifique-se que seu Repository tenha um método ou @Query para trazer os itens junto se necessário
        // ou use o findAll padrão (lazy loading trará os itens quando acessados, se dentro de transação)
        return orcamentoRepository.findAll(); 
    }

    @Transactional
    public OrcamentoEntity alterar(Integer id, OrcamentoEntity orcamento){
        Optional<OrcamentoEntity> orcamentoExistenteOpt = orcamentoRepository.findById(id);
        
        if (orcamentoExistenteOpt.isPresent()) {
            OrcamentoEntity orcamentoAtualizado = orcamentoExistenteOpt.get();
            
            // Atualiza campos simples
            orcamentoAtualizado.setDescricao(orcamento.getDescricao());
            orcamentoAtualizado.setDataCancel(orcamento.getDataCancel());
            orcamentoAtualizado.setDataOrcamento(orcamento.getDataOrcamento());
            orcamentoAtualizado.setStatus(orcamento.getStatus());
            orcamentoAtualizado.setCliente(orcamento.getCliente());
            orcamentoAtualizado.setUsuario(orcamento.getUsuario());
            
            // Atualiza a lista de itens (Limpa e readiciona para garantir sincronia)
            orcamentoAtualizado.getItens().clear();
            if (orcamento.getItens() != null) {
                orcamentoAtualizado.getItens().addAll(orcamento.getItens());
            }
            
            // Re-vincula os produtos corretamente
            prepararItens(orcamentoAtualizado);

            return orcamentoRepository.save(orcamentoAtualizado);
        } else {
            return null;
        }
    }

    // Método auxiliar para vincular Produtos e o Próprio Orçamento aos itens
    private void prepararItens(OrcamentoEntity orcamento) {
        if (orcamento.getItens() != null && !orcamento.getItens().isEmpty()) {
            List<OrcamentoProdutoEntity> itensParaRemover = new ArrayList<>();

            for (OrcamentoProdutoEntity item : orcamento.getItens()) {
                // 1. Busca o produto real no banco pelo ID vindo da tela
                if (item.getProduto() != null && item.getProduto().getIdProduto() != null) {
                    ProdutoEntity produtoReal = produtoRepository.findById(item.getProduto().getIdProduto())
                        .orElse(null);
                    
                    if (produtoReal != null) {
                        item.setProduto(produtoReal);
                        item.setOrcamento(orcamento); // Vincula o pai (Orçamento) ao filho (Item)
                    } else {
                        // Se o produto não existe, marcamos para remover da lista para não dar erro
                        itensParaRemover.add(item);
                    }
                } else {
                    itensParaRemover.add(item);
                }
            }
            orcamento.getItens().removeAll(itensParaRemover);
        }
    }
}