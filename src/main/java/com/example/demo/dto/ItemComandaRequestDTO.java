package com.example.demo.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public record ItemComandaRequestDTO(


        @NotNull(message = "Id não pode ser nulo.")
        Long produtoId,

        @Min(value = 1, message = "Valor mínimo: 1")
        Integer
        quantidade
) {
}