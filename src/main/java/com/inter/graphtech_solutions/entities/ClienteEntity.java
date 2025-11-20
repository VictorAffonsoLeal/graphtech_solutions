package com.inter.graphtech_solutions.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "id_pessoa")
// REMOVIDO @JsonIdentityInfo daqui pois ele já é herdado de PessoaEntity corretamente
public class ClienteEntity extends PessoaEntity {

    @NonNull
    private String endereco;
    
    @NonNull
    private String telefone;
    
    @NonNull
    private String email;
    
    @NonNull
    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private List<OrcamentoEntity> orcamentos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private List<PedidoEntity> pedidos = new ArrayList<>();

}