package com.example.demo.dto;
import com.example.demo.auth.Perfil;
public record UsuarioRequestDTO(String username, String senha, Perfil perfil) {}
