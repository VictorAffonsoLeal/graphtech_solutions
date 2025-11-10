package com.inter.graphtech_solutions.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.OrcamentoEntity;
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
        vincularProdutosExistentes(orcamento, new ArrayList<>(orcamento.getProdutos()));
        return orcamentoRepository.save(orcamento);
    }

    public void excluir(int id){
        orcamentoRepository.deleteById(id);
    }

    public List<OrcamentoEntity> listarOrcamento(){
        return orcamentoRepository.findAllWithDetails();
    }

    public OrcamentoEntity alterar(int id, OrcamentoEntity orcamento){
        Optional<OrcamentoEntity> orcamentoExistente = orcamentoRepository.findById(id);
        if (orcamentoExistente.isPresent()) {
            OrcamentoEntity orcamentoAtualizado = orcamentoExistente.get();
            orcamentoAtualizado.setDescricao(orcamento.getDescricao());
            orcamentoAtualizado.setDataCancel(orcamento.getDataCancel());
            orcamentoAtualizado.setDataOrcamento(orcamento.getDataOrcamento());
            orcamentoAtualizado.setStatus(orcamento.isStatus());
            orcamentoAtualizado.setCliente(orcamento.getCliente());
            orcamentoAtualizado.setUsuario(orcamento.getUsuario());
            vincularProdutosExistentes(orcamentoAtualizado, new ArrayList<>(orcamento.getProdutos()));

            //orcamentoAtualizado.setProdutosList(orcamento.getProdutosList());
            return orcamentoRepository.save(orcamentoAtualizado);
        } else {
            return null;
        }
    }

    private void vincularProdutosExistentes(OrcamentoEntity orcamento, List<ProdutoEntity> produtos) {
        if (produtos != null && !produtos.isEmpty()) {
            // Busca os produtos REAIS do banco de dados pelos IDs
            // CORREÇÃO: Coletar para um Set
            Set<ProdutoEntity> produtosGerenciados = produtos.stream()
                .map(produto -> produtoRepository.findById(produto.getIdProduto()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            
            orcamento.setProdutos(produtosGerenciados); // Define o Set de produtos gerenciados
        } else {
            orcamento.setProdutos(new HashSet<>()); // Limpa a lista se ela vier vazia
        }
    }


}
