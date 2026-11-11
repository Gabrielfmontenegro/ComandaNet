package com.example.demo.dto;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        String nome,
        String descricao,
        BigDecimal preco,
        Integer estoque
) {
}