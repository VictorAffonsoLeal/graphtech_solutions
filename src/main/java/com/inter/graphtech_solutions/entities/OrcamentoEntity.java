package com.inter.graphtech_solutions.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "orcamentos")
public class OrcamentoEntity {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idOrcamento;
    @NonNull
    private String descricao;
    private LocalDate dataCancel;
    @NonNull
    private LocalDate dataOrcamento;
    private boolean status;

    // Relacionamento N:1 (Muitos Orçamentos para Um Cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    // Relacionamento N:1 (Muitos Orçamentos para Um Usuário)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    // Lado inverso da relação 1:1 com Pedido
    @OneToOne(mappedBy = "orcamento", fetch = FetchType.LAZY)
    private PedidoEntity pedido;

    // Um Orcamento tem muitos Produtos (1:N) — lado inverso
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "orcamento_produto", // Nome da tabela de junção
        joinColumns = @JoinColumn(name = "orcamento_id"), // Chave desta entidade
        inverseJoinColumns = @JoinColumn(name = "produto_id") // Chave da outra entidade
    )   
    private Set<ProdutoEntity> produtos = new HashSet<>();

}
