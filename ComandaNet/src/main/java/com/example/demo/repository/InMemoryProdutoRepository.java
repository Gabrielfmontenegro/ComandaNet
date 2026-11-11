package com.example.demo.repository;

import com.example.demo.model.Produto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryProdutoRepository implements ProdutoRepository {

    private final Map<Long, Produto> banco = new LinkedHashMap<>();

    @Override
    public Produto salvar(Produto produto) {
        log.info("[REPOSITORY] Salvando produto: {}", produto.getId());
        banco.put(produto.getId(), produto);
        return produto;
    }

    @Override
    public List<Produto> listarTodos() {
        log.info("[REPOSITORY] Listando produtos");
        return new ArrayList<>(banco.values());
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        log.info("[REPOSITORY] Buscando produto: {}", id);
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public Optional<Produto> deletar(Long id) {
        log.info("[REPOSITORY] Excluindo produto: {}", id);
        return Optional.ofNullable(banco.remove(id));
    }

    @Override
    public Produto atualizar(Produto produto) {
        log.info("[REPOSITORY] Atualizando produto: {}", produto.getId());
        banco.put(produto.getId(), produto);
        return produto;
    }
}