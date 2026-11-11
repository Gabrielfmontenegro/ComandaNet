package com.example.demo.dto;

import java.util.List;

public record ComandaRequestDTO(
        String nome,
        List<ItemComandaRequestDTO> itens
) {}
