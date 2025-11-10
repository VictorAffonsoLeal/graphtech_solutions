package com.inter.graphtech_solutions.entities;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "produtos")
public class ProdutoEntity {

        // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProduto;
    @NonNull
    private String descricao;
    private float valor;
    private int qtd;
    
    @JsonIgnore // Ignora ao serializar para evitar loops
    @ManyToMany(mappedBy = "produtos", fetch = FetchType.LAZY)
    private Set<PedidoEntity> pedidos = new HashSet<>();

    // Produto pertence a um Orcamento (N:1)
    @JsonIgnore // Ignora ao serializar para evitar loops
    @ManyToMany(mappedBy = "produtos", fetch = FetchType.LAZY)
    private Set<OrcamentoEntity> orcamentos = new HashSet<>();

}
