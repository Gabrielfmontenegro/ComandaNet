
package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comandas")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "itens_comanda",
            joinColumns = @JoinColumn(name = "comanda_id")
    )
    private List<ItemComanda> itens = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    // Construtor vazio exigido pelo JPA
    protected Comanda() {
    }

    // Construtor principal
    public Comanda(
            Long id,
            String nome,
            Status status,
            List<ItemComanda> itens,
            BigDecimal total,
            LocalDateTime dataEmissao) {

        this.id = id;
        this.nome = nome;
        this.status = status;
        this.itens = itens;
        this.total = total;
        this.dataEmissao = dataEmissao;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Status getStatus() {
        return status;
    }

    public List<ItemComanda> getItens() {
        return itens;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setItens(List<ItemComanda> itens) {
        this.itens = itens;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
