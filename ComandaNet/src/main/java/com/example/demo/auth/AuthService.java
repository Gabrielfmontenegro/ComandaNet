package com.example.demo.auth;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final Map<String, UsuarioSistema> usuarios = new ConcurrentHashMap<>();

    public AuthService() {
        cadastrarInicial("admin", "Admin@12345", Perfil.ADM);
        cadastrarInicial("usuario", "Usuario@12345", Perfil.UC);
    }

    private void cadastrarInicial(String username, String senha, Perfil perfil) {
        usuarios.put(username, new UsuarioSistema(
                username, SenhaHasher.gerar(senha), perfil));
    }

    public UsuarioSistema autenticar(String username, String senha) {
        if (username == null || senha == null) {
            throw new IllegalArgumentException("Informe usuário e senha.");
        }
        UsuarioSistema usuario = usuarios.get(username.trim());
        if (usuario == null || !SenhaHasher.verificar(senha, usuario.senhaHash())) {
            throw new IllegalArgumentException("Usuário ou senha inválidos.");
        }
        return usuario;
    }
}
