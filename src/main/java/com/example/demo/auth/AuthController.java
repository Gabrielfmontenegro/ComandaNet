package com.example.demo.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest body,
                                                HttpServletRequest request) {
        UsuarioSistema usuario = authService.autenticar(body.username(), body.senha());
        HttpSession sessao = request.getSession(true);
        request.changeSessionId();
        sessao.setAttribute("usuarioLogado", usuario.username());
        sessao.setAttribute("perfil", usuario.perfil().name());
        return ResponseEntity.ok(new LoginResponse(usuario.username(), usuario.perfil()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null || sessao.getAttribute("usuarioLogado") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Faça login para continuar."));
        }
        return ResponseEntity.ok(new LoginResponse(
                (String) sessao.getAttribute("usuarioLogado"),
                Perfil.valueOf((String) sessao.getAttribute("perfil"))));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) sessao.invalidate();
        return ResponseEntity.noContent().build();
    }
}
