package com.example.demo.repository;

import com.example.demo.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    default Produto salvar(Produto p){ return save(p); }
    default java.util.List<Produto> listarTodos(){ return findAll(); }
    default Optional<Produto> buscarPorId(Long id){ return findById(id); }
    default Optional<Produto> deletar(Long id){
        Optional<Produto> p=findById(id); p.ifPresent(this::delete); return p;
    }
    default Produto atualizar(Produto p){ return save(p); }
}
