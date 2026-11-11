package com.example.demo.auth;

public record UsuarioSistema(String username, String senhaHash, Perfil perfil) {
}
