package com.example.demo.dto;


import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;


public record ItemComandaResponseDTO(

        @NotNull(message = "Id não pode ser nulo.")
        Long produtoId,


        @NotBlank(message = "o nome do produto é obrigatorio.")
        String nomeProduto,


        @Min (value = 1, message = "quantidade minima: 1")
        Integer quantidade,


        @Min (value = 1, message = "unidade minima por produto: 1")
        BigDecimal precoUnitario,

        @Min (value = 1, message = "valor minimo: 1")
        BigDecimal
        subtotal
) {
}