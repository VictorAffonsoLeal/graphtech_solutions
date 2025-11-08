package com.inter.graphtech_solutions.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inter.graphtech_solutions.entities.OrcamentoEntity;
import com.inter.graphtech_solutions.repositories.OrcamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrcamentoService {
    private final OrcamentoRepository orcamentoRepository;

    public OrcamentoEntity salvar(OrcamentoEntity orcamento) {
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

            //orcamentoAtualizado.setProdutosList(orcamento.getProdutosList());
            return orcamentoRepository.save(orcamentoAtualizado);
        } else {
            return null;
        }
    }


}
