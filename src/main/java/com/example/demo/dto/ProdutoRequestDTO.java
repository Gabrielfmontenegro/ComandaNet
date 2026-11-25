package com.example.demo.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoRequestDTO(

        @NotBlank(message = "O nome do produto é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @NotBlank(message = "A descrição na comanda é obrigatório.")
        @Size(max = 100, message = "A descrição deve ter no máximo 100 caracteres.")
        String descricao,

        @NotNull(message = "O total da comanda é obrigatório.")
        @PositiveOrZero(message = "O preço não pode ser negativo.")
        BigDecimal preco,

        @Min(value = 1, message = "Valor mínimo: 1")
        Integer estoque
) {
}