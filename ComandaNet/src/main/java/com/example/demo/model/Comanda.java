package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Comanda {
    private Long id;
    private String nome;
    private String status;
    private List<ItemComanda> itens;
    private BigDecimal total;
    private LocalDateTime dataEmissao;

    public Comanda(Long id, String nome, String status, List<ItemComanda> itens,
                   BigDecimal total, LocalDateTime dataEmissao) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.itens = itens;
        this.total = total;
        this.dataEmissao = dataEmissao;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getStatus() { return status; }
    public List<ItemComanda> getItens() { return itens; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getDataEmissao() { return dataEmissao; }

    public void setNome(String nome) { this.nome = nome; }
    public void setStatus(String status) { this.status = status; }
    public void setItens(List<ItemComanda> itens) { this.itens = itens; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
