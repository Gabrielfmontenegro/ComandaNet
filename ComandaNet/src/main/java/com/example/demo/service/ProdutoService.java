package com.example.demo.service;

import com.example.demo.dto.ProdutoRequestDTO;
import com.example.demo.dto.ProdutoResponseDTO;
import com.example.demo.model.Produto;
import com.example.demo.repository.InMemoryProdutoRepository;
import com.example.demo.repository.ProdutoRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final AtomicLong sequencia = new AtomicLong();

    public ProdutoService(InMemoryProdutoRepository repository) {
        this.repository = repository;
    }

    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validar(dto);

        Produto produto = new Produto(
                sequencia.incrementAndGet(),
                dto.nome().trim(),
                dto.descricao(),
                dto.preco(),
                dto.estoque()
        );

        Produto salvo = repository.salvar(produto);

        log.info("[SERVICE] Produto cadastrado: {}", salvo.getId());

        return toResponseDTO(salvo);
    }

    public List<ProdutoResponseDTO> listar() {
        log.info("[SERVICE] Listando produtos");

        List<ProdutoResponseDTO> respostas = new ArrayList<>();

        for (Produto produto : repository.listarTodos()) {
            respostas.add(toResponseDTO(produto));
        }

        return respostas;
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        log.info("[SERVICE] Buscando produto: {}", id);

        Produto produto = repository.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado: " + id
                        )
                );

        return toResponseDTO(produto);
    }

    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        validar(dto);

        Produto existente = repository.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado: " + id
                        )
                );

        existente.setNome(dto.nome().trim());
        existente.setDescricao(dto.descricao());
        existente.setPreco(dto.preco());
        existente.setEstoque(dto.estoque());

        Produto atualizado = repository.atualizar(existente);

        log.info("[SERVICE] Produto atualizado: {}", id);

        return toResponseDTO(atualizado);
    }

    public ProdutoResponseDTO deletar(Long id) {
        log.info("[SERVICE] Excluindo produto: {}", id);

        Produto removido = repository.deletar(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado: " + id
                        )
                );

        return toResponseDTO(removido);
    }

    private void validar(ProdutoRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException(
                    "Os dados do produto são obrigatórios."
            );
        }

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do produto é obrigatório."
            );
        }

        if (dto.preco() == null
                || dto.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior que zero."
            );
        }

        if (dto.estoque() == null || dto.estoque() < 0) {
            throw new IllegalArgumentException(
                    "O estoque não pode ser negativo."
            );
        }
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getEstoque()
        );
    }
}