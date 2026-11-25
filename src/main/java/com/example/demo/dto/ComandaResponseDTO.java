package com.example.demo.dto;

import com.example.demo.model.Produto;
import com.example.demo.model.Status;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ComandaResponseDTO(

        @NotNull(message = "O ID da comanda é obrigatório.")
        Long id,

        @NotBlank(message = "O nome da comanda é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @NotNull(message = "O status é obrigatório")
        Status status,

        @NotNull(message = "A lista de itens é obrigatória.")
        @Size(min = 1, message = "A lista tem que conter pelo menos 1 item")
        List<ItemComandaResponseDTO> Produto,

        @NotNull(message = "O total da comanda é obrigatório.")
        @PositiveOrZero(message = "O total não pode ser negativo.")
        BigDecimal total,

        @NotNull(message = "A data de emissão é obrigatória.")
        @PastOrPresent
        LocalDateTime dataEmissao
) {}
