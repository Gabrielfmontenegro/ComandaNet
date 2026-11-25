package com.example.demo.controller;

import com.example.demo.dto.ComandaRequestDTO;
import com.example.demo.dto.ComandaResponseDTO;
import com.example.demo.service.ComandaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comandas")
public class ComandaController {
    private final ComandaService service;

    public ComandaController(ComandaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ComandaResponseDTO> emitir(@RequestBody ComandaRequestDTO corpo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.emitir(corpo));
    }

    @GetMapping
    public ResponseEntity<List<ComandaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComandaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComandaResponseDTO> atualizar(
            @PathVariable Long id, @RequestBody ComandaRequestDTO corpo) {
        return ResponseEntity.ok(service.atualizar(id, corpo));
    }

    @PatchMapping("/{id}/nome")
    public ResponseEntity<ComandaResponseDTO> renomear(
            @PathVariable Long id, @RequestBody Map<String, String> corpo) {
        return ResponseEntity.ok(service.renomear(id, corpo.get("nome")));
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<ComandaResponseDTO> fechar(@PathVariable Long id) {
        return ResponseEntity.ok(service.fechar(id));
    }
}
