package com.example.demo.repository;

import com.example.demo.model.Comanda;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryComandaRepository
        implements ComandaRepository {

    private final Map<Long, Comanda> banco = new LinkedHashMap<>();

    @Override
    public Comanda salvar(Comanda comanda) {
        banco.put(comanda.getId(), comanda);
        return comanda;
    }

    @Override
    public List<Comanda> listarTodas() {
        return new ArrayList<>(banco.values());
    }

    @Override
    public Optional<Comanda> buscarPorId(Long id) {
        return Optional.ofNullable(banco.get(id));
    }
}