package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String usuario = (String) (request.getSession(false) == null ? null
                : request.getSession(false).getAttribute("usuarioLogado"));
        if (usuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"Faça login para continuar.\"}");
            return false;
        }

        String caminho = request.getRequestURI();
        String metodo = request.getMethod();
        boolean leitura = "GET".equalsIgnoreCase(metodo)
                || "HEAD".equalsIgnoreCase(metodo)
                || "OPTIONS".equalsIgnoreCase(metodo);
        String perfil = (String) request.getSession(false).getAttribute("perfil");
        boolean alteracaoProduto = caminho.startsWith("/produtos") && !leitura;
        boolean alteracaoComandaExistente = caminho.startsWith("/comandas/") && !leitura;
        if ((alteracaoProduto || alteracaoComandaExistente) && !"ADM".equals(perfil)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"Somente administradores podem alterar produtos ou editar/fechar comandas.\"}");
            return false;
        }
        return true;
    }
}
