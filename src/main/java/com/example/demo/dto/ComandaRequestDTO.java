package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ComandaRequestDTO(

        @NotBlank(message = "O nome da comanda é obrigatório.")
        @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres.")
        String nome,

        @NotEmpty(message = "A comanda deve possuir pelo menos um item.")
        List<@Valid ItemComandaRequestDTO> itens

) {}