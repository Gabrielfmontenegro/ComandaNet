package com.example.demo.dto;
import com.example.demo.auth.Perfil;
import com.example.demo.auth.UsuarioSistema;
public record UsuarioResponseDTO(String username, Perfil perfil) {
    public static UsuarioResponseDTO de(UsuarioSistema u) { return new UsuarioResponseDTO(u.username(), u.perfil()); }
}
