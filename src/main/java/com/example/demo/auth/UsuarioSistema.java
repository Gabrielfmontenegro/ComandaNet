package com.example.demo.auth;

import jakarta.persistence.*;

@Entity
@Table(name="usuarios")
public class UsuarioSistema {
    @Id
    @Column(name="username", nullable=false, length=60)
    private String username;
    @Column(name="senha_hash", nullable=false, length=300)
    private String senhaHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private Perfil perfil;

    protected UsuarioSistema() {}
    public UsuarioSistema(String username, String senhaHash, Perfil perfil) {
        this.username=username; this.senhaHash=senhaHash; this.perfil=perfil;
    }
    public String username(){return username;}
    public String senhaHash(){return senhaHash;}
    public Perfil perfil(){return perfil;}
}
