package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class TratadorDeExcecoes {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarArgumentoInvalido(
            IllegalArgumentException ex
    ) {
        log.warn("[ERRO] Argumento inválido: {}", ex.getMessage());

        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> tratarCorpoInvalido(
            HttpMessageNotReadableException ex
    ) {
        log.warn("[ERRO] Corpo da requisição inválido");

        return resposta(
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido. Verifique o JSON enviado."
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> tratarTipoInvalido(
            MethodArgumentTypeMismatchException ex
    ) {
        log.warn("[ERRO] Tipo de parâmetro inválido: {}", ex.getName());

        return resposta(
                HttpStatus.BAD_REQUEST,
                "O parâmetro '" + ex.getName() + "' possui um tipo inválido."
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(
            MethodArgumentNotValidException ex
    ) {
        log.warn("[ERRO] Falha na validação da requisição");

        return resposta(
                HttpStatus.BAD_REQUEST,
                "Os dados enviados não passaram pela validação."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarErroInterno(
            Exception ex
    ) {
        log.error("[ERRO] Erro interno na aplicação", ex);

        return resposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor."
        );
    }

    private ResponseEntity<Map<String, String>> resposta(
            HttpStatus status,
            String mensagem
    ) {
        return ResponseEntity
                .status(status)
                .body(Map.of("erro", mensagem != null
                        ? mensagem
                        : "Erro não especificado."));
    }
}