package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Embeddable
public class ItemComanda {
    @Column(name="produto_id")
    private Long produtoId;
    @Column(name="nome_produto", nullable=false, length=120)
    private String nomeProduto;
    @Column(nullable=false)
    private Integer quantidade;
    @Column(name="preco_unitario", nullable=false, precision=10, scale=2)
    private BigDecimal precoUnitario;
    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal subtotal;

    protected ItemComanda() {}
    public ItemComanda(Long produtoId, String nomeProduto, Integer quantidade,
                       BigDecimal precoUnitario, BigDecimal subtotal) {
        this.produtoId=produtoId; this.nomeProduto=nomeProduto; this.quantidade=quantidade;
        this.precoUnitario=precoUnitario; this.subtotal=subtotal;
    }
    public Long getProdutoId(){return produtoId;}
    public String getNomeProduto(){return nomeProduto;}
    public Integer getQuantidade(){return quantidade;}
    public BigDecimal getPrecoUnitario(){return precoUnitario;}
    public BigDecimal getSubtotal(){return subtotal;}
}
