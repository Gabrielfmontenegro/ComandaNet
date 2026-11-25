package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/api")
    public Map<String, String> inicio() {
        return Map.of(
                "mensagem", "API do ComandaNet",
                "status", "online",
                "produtos", "/produtos",
                "comandas", "/comandas",
                "login", "/auth/login"
        );
    }
}
