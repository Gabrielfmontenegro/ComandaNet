package com.example.demo.repository;

import com.example.demo.auth.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioSistema, String> {}
