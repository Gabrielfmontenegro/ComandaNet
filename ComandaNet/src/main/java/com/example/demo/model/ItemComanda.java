package com.example.demo.model;

import java.math.BigDecimal;

public class ItemComanda {

    private Long produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public ItemComanda(Long produtoId, String nomeProduto,
                    Integer quantidade, BigDecimal precoUnitario,
                    BigDecimal subtotal) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}