package com.inter.graphtech_solutions.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.ProdutoEntity;
import com.inter.graphtech_solutions.repositories.ProdutoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoEntity salvarProduto(ProdutoEntity produto) {
        return produtoRepository.save(produto);
    }

    public List<ProdutoEntity> listarProdutos() {
        return produtoRepository.findAllWithDetails();
    }

    public void excluirProduto(int id) {
        produtoRepository.deleteById(id);
    }

    public ProdutoEntity alterarProduto(int id, ProdutoEntity produto){
        Optional<ProdutoEntity> produtoExistente = produtoRepository.findById(id);
        if (produtoExistente.isPresent()) {
            ProdutoEntity produtoAtualizado = produtoExistente.get();
            produtoAtualizado.setDescricao(produto.getDescricao());
            produtoAtualizado.setValor(produto.getValor());
            produtoAtualizado.setQtd(produto.getQtd());

            return produtoRepository.save(produtoAtualizado);
        } else {
            return null;
        }
    }

}
