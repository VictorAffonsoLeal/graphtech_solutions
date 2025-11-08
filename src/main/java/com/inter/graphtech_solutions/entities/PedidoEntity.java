package com.inter.graphtech_solutions.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "pedidos")

public class PedidoEntity {

    // Atributos
    @Id // chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPedido;
    @NonNull
    private String descricao;
    private LocalDate dataCancel;
    @NonNull
    private LocalDate dataPedido;

    // Relacionamento N:1 (Muitos Pedidos para Um Cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente")
    private ClienteEntity cliente;

    // Relacionamento N:1 (Muitos Pedidos para Um Usuário)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario")
    private UsuarioEntity usuario;

    // Relacionamento 1:N (Um Pedido para Muitos Produtos)
    @JsonIgnore
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProdutoEntity> produtos = new ArrayList<>();

    // Relação 1:1 com Orçamento
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrcamento", unique = true)
    private OrcamentoEntity orcamento;

}
