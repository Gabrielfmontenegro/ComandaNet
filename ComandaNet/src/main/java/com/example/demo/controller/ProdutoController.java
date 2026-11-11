package com.example.demo.controller;

import com.example.demo.dto.ProdutoRequestDTO;
import com.example.demo.dto.ProdutoResponseDTO;
import com.example.demo.service.ProdutoService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(
            @RequestBody ProdutoRequestDTO corpo
    ) {
        log.info("[CONTROLLER] POST /produtos");

        ProdutoResponseDTO produto = service.criar(corpo);

        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listar() {
        log.info("[CONTROLLER] GET /produtos");

        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscar(
            @PathVariable Long id
    ) {
        log.info("[CONTROLLER] GET /produtos/{}", id);

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ProdutoRequestDTO corpo
    ) {
        log.info("[CONTROLLER] PUT /produtos/{}", id);

        return ResponseEntity.ok(service.atualizar(id, corpo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> deletar(
            @PathVariable Long id
    ) {
        log.info("[CONTROLLER] DELETE /produtos/{}", id);

        return ResponseEntity.ok(service.deletar(id));
    }
}
