package com.example.demo.repository;

import com.example.demo.model.Comanda;

import java.util.List;
import java.util.Optional;

public interface ComandaRepository {

    Comanda salvar(Comanda comanda);

    List<Comanda> listarTodas();

    Optional<Comanda> buscarPorId(Long id);
}