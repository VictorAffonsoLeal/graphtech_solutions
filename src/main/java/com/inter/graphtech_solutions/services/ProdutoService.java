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
        // Garante que nasce ativo se status for nulo
        if (produto.getStatus() == null) {
            produto.setStatus(0);
        }
        return produtoRepository.save(produto);
    }

    public List<ProdutoEntity> listarProdutos() {
        // Retorna apenas os ativos (status = 0)
        // Certifique-se que o ProdutoRepository tenha o método findByStatus(Integer status)
        // Caso não tenha, você pode usar findAll() e filtrar aqui, ou adicionar no repositório.
        // Assumindo que você adicionou findByStatus no repositório conforme recomendado anteriormente.
        return produtoRepository.findByStatus(0);
    }
    
    // Método para listagem completa (se precisar no futuro para admin)
    public List<ProdutoEntity> listarTodosIncluindoDeletados() {
        return produtoRepository.findAll();
    }

    public void excluirProduto(Integer id) {
        Optional<ProdutoEntity> produtoOpt = produtoRepository.findById(id);
        if (produtoOpt.isPresent()) {
            ProdutoEntity produto = produtoOpt.get();
            produto.setStatus(1); // 1 = Inativo/Deletado
            produtoRepository.save(produto);
        }
    }

    public ProdutoEntity alterarProduto(Integer id, ProdutoEntity produto) {
        Optional<ProdutoEntity> produtoExistente = produtoRepository.findById(id);
        if (produtoExistente.isPresent()) {
            ProdutoEntity atual = produtoExistente.get();
            atual.setDescricao(produto.getDescricao());
            atual.setValor(produto.getValor());
            atual.setQtd(produto.getQtd());
            // Mantém o status original (não altera se estava ativo ou inativo acidentalmente)
            
            return produtoRepository.save(atual);
        }
        return null;
    }
}