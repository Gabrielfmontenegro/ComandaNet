package com.example.demo.controller;

import com.example.demo.auth.AuthService;
import com.example.demo.auth.UsuarioSistema;
import com.example.demo.dto.UsuarioRequestDTO;
import com.example.demo.dto.UsuarioResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final AuthService auth;
    public UsuarioController(AuthService auth) { this.auth = auth; }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return auth.listarUsuarios().stream().map(UsuarioResponseDTO::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO cadastrar(@RequestBody UsuarioRequestDTO dto) {
        UsuarioSistema usuario = auth.cadastrar(dto.username(), dto.senha(), dto.perfil());
        return UsuarioResponseDTO.de(usuario);
    }
}
