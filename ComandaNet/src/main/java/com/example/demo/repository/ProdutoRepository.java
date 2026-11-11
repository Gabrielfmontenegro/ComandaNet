package com.example.demo.repository;

import com.example.demo.model.Produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {

    Produto salvar(Produto produto);

    List<Produto> listarTodos();

    Optional<Produto> buscarPorId(Long id);

    Optional<Produto> deletar(Long id);

    Produto atualizar(Produto produto);
}