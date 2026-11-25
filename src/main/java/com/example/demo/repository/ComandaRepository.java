package com.example.demo.repository;

import com.example.demo.model.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    default Comanda salvar(Comanda c){ return save(c); }
    default java.util.List<Comanda> listarTodas(){ return findAll(); }
    default Optional<Comanda> buscarPorId(Long id){ return findById(id); }
}
