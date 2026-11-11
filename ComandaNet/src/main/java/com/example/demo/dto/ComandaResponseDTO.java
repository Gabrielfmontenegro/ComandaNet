package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ComandaResponseDTO(
        Long id,
        String nome,
        String status,
        List<ItemComandaResponseDTO> itens,
        BigDecimal total,
        LocalDateTime dataEmissao
) {}
