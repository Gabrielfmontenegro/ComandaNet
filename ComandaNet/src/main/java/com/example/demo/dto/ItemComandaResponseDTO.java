package com.example.demo.dto;

import java.math.BigDecimal;

public record ItemComandaResponseDTO(
        Long produtoId,
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
}