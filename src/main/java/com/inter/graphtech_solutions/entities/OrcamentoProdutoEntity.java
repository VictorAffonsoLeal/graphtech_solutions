package com.inter.graphtech_solutions.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orcamento_produto")
public class OrcamentoProdutoEntity {

    @EmbeddedId
    private OrcamentoProdutoId id = new OrcamentoProdutoId();

    @JsonIgnore
    @ManyToOne
    @MapsId("orcamentoId")
    @JoinColumn(name = "orcamento_id")
    private OrcamentoEntity orcamento;

    @ManyToOne
    @MapsId("produtoId")
    @JoinColumn(name = "produto_id")
    private ProdutoEntity produto;

    private Integer qtd;
    private Double valorUnitario;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class OrcamentoProdutoId implements Serializable {
        private Integer orcamentoId;
        private Integer produtoId;
    }
}