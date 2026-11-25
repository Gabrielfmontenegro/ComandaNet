package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(length = 500)
    private String descricao;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;
    @Column(nullable = false)
    private Integer estoque;

    protected Produto() {}
    public Produto(Long id, String nome, String descricao, BigDecimal preco, Integer estoque) {
        this.id=id; this.nome=nome; this.descricao=descricao; this.preco=preco; this.estoque=estoque;
    }
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNome(){return nome;}
    public void setNome(String nome){this.nome=nome;}
    public String getDescricao(){return descricao;}
    public void setDescricao(String descricao){this.descricao=descricao;}
    public BigDecimal getPreco(){return preco;}
    public void setPreco(BigDecimal preco){this.preco=preco;}
    public Integer getEstoque(){return estoque;}
    public void setEstoque(Integer estoque){this.estoque=estoque;}
}
