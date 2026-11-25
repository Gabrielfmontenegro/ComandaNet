package com.example.demo.auth;

import com.example.demo.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService implements ApplicationRunner {
    private final UsuarioRepository usuarios;

    public AuthService(UsuarioRepository usuarios) { this.usuarios = usuarios; }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarios.count() == 0) {
            usuarios.save(new UsuarioSistema("admin", SenhaHasher.gerar("Admin@12345"), Perfil.ADM));
            usuarios.save(new UsuarioSistema("usuario", SenhaHasher.gerar("Usuario@12345"), Perfil.UC));
        }
    }

    @Transactional(readOnly=true)
    public UsuarioSistema autenticar(String username, String senha) {
        if (username == null || senha == null || username.isBlank() || senha.isBlank())
            throw new IllegalArgumentException("Informe usuário e senha.");
        UsuarioSistema usuario = usuarios.findById(username.trim()).orElse(null);
        if (usuario == null || !SenhaHasher.verificar(senha, usuario.senhaHash()))
            throw new IllegalArgumentException("Usuário ou senha inválidos.");
        return usuario;
    }

    @Transactional
    public UsuarioSistema cadastrar(String username, String senha, Perfil perfil) {
        if (username == null || !username.matches("[A-Za-z0-9_.-]{3,60}"))
            throw new IllegalArgumentException("O usuário deve ter de 3 a 60 caracteres (letras, números, ponto, hífen ou _).");
        if (senha == null || senha.length() < 8)
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        if (perfil == null) throw new IllegalArgumentException("Selecione o perfil do usuário.");
        String chave = username.trim();
        if (usuarios.existsById(chave)) throw new IllegalArgumentException("Este usuário já existe.");
        return usuarios.save(new UsuarioSistema(chave, SenhaHasher.gerar(senha), perfil));
    }

    @Transactional(readOnly=true)
    public List<UsuarioSistema> listarUsuarios() { return usuarios.findAll(); }
}
