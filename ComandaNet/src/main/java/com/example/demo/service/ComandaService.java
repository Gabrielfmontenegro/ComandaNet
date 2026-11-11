
package com.example.demo.service;

import com.example.demo.dto.ItemComandaRequestDTO;
import com.example.demo.dto.ItemComandaResponseDTO;
import com.example.demo.dto.ComandaRequestDTO;
import com.example.demo.dto.ComandaResponseDTO;
import com.example.demo.model.ItemComanda;
import com.example.demo.model.Comanda;
import com.example.demo.model.Produto;
import com.example.demo.repository.InMemoryComandaRepository;
import com.example.demo.repository.ProdutoRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ComandaService {

    private final InMemoryComandaRepository comandaRepository;
    private final ProdutoRepository produtoRepository;
    private final AtomicLong sequencia = new AtomicLong();

    public ComandaService(
            InMemoryComandaRepository comandaRepository,
            ProdutoRepository produtoRepository
    ) {
        this.comandaRepository = comandaRepository;
        this.produtoRepository = produtoRepository;
    }

    public synchronized ComandaResponseDTO emitir(
            ComandaRequestDTO dto
    ) {
        if (dto == null
                || dto.itens() == null
                || dto.itens().isEmpty()) {
            throw new IllegalArgumentException(
                    "A comanda precisa ter pelo menos um produto."
            );
        }
        String nome = validarNome(dto.nome());

        Map<Long, Produto> produtos = new LinkedHashMap<>();
        Map<Long, Integer> quantidades = new LinkedHashMap<>();
        List<ItemComanda> itensComanda = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // FASE 1: validar todos os itens sem alterar o estoque.
        for (ItemComandaRequestDTO itemDTO : dto.itens()) {
            if (itemDTO == null
                    || itemDTO.produtoId() == null
                    || itemDTO.quantidade() == null
                    || itemDTO.quantidade() <= 0) {
                throw new IllegalArgumentException(
                        "Cada item precisa de um produto válido "
                                + "e uma quantidade maior que zero."
                );
            }

            Long produtoId = itemDTO.produtoId();

            Produto produto = produtos.get(produtoId);

            if (produto == null) {
                produto = produtoRepository.buscarPorId(produtoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Produto não encontrado: "
                                                + produtoId
                                )
                        );

                produtos.put(produtoId, produto);
            }

            int quantidadeAnterior =
                    quantidades.getOrDefault(produtoId, 0);

            int quantidadeNova;

            try {
                quantidadeNova = Math.addExact(
                        quantidadeAnterior,
                        itemDTO.quantidade()
                );
            } catch (ArithmeticException erro) {
                throw new IllegalArgumentException(
                        "Quantidade total inválida para o produto: "
                                + produto.getNome()
                );
            }

            quantidades.put(produtoId, quantidadeNova);

            BigDecimal precoUnitario = produto.getPreco()
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal subtotal = precoUnitario
                    .multiply(BigDecimal.valueOf(itemDTO.quantidade()))
                    .setScale(2, RoundingMode.HALF_UP);

            itensComanda.add(new ItemComanda(
                    produto.getId(),
                    produto.getNome(),
                    itemDTO.quantidade(),
                    precoUnitario,
                    subtotal
            ));

            total = total.add(subtotal);
        }

        // FASE 2: verificar o estoque total necessário de cada produto.
        for (Map.Entry<Long, Integer> entrada
                : quantidades.entrySet()) {

            Produto produto = produtos.get(entrada.getKey());
            int quantidadeNecessaria = entrada.getValue();

            if (produto.getEstoque() == null
                    || produto.getEstoque() < quantidadeNecessaria) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto '"
                                + produto.getNome()
                                + "'. Disponível: "
                                + produto.getEstoque()
                                + "; solicitado: "
                                + quantidadeNecessaria
                                + ". Nenhum estoque foi alterado."
                );
            }
        }

        // FASE 3: somente após todas as validações, baixar o estoque.
        for (Map.Entry<Long, Integer> entrada
                : quantidades.entrySet()) {

            Produto produto = produtos.get(entrada.getKey());

            produto.setEstoque(
                    produto.getEstoque() - entrada.getValue()
            );

            produtoRepository.atualizar(produto);
        }

        // FASE 4: salvar a comanda com os itens e o total calculado.
        total = total.setScale(2, RoundingMode.HALF_UP);

        long id = sequencia.incrementAndGet();
        Comanda comanda = new Comanda(
                id,
                nome,
                "ABERTA",
                itensComanda,
                total,
                LocalDateTime.now()
        );

        return converter(comandaRepository.salvar(comanda));
    }

    public synchronized ComandaResponseDTO atualizar(Long id, ComandaRequestDTO dto) {
        Comanda existente = obterComanda(id);
        garantirAberta(existente);
        if (dto == null || dto.itens() == null || dto.itens().isEmpty()) {
            throw new IllegalArgumentException("A comanda precisa ter pelo menos um produto.");
        }

        String nome = validarNome(dto.nome());
        Map<Long, Integer> quantidadesAntigas = new LinkedHashMap<>();
        for (ItemComanda item : existente.getItens()) {
            quantidadesAntigas.merge(item.getProdutoId(), item.getQuantidade(), Integer::sum);
        }

        Map<Long, Produto> produtos = new LinkedHashMap<>();
        Map<Long, Integer> quantidadesNovas = new LinkedHashMap<>();
        List<ItemComanda> novosItens = new ArrayList<>();
        BigDecimal novoTotal = BigDecimal.ZERO;

        for (ItemComandaRequestDTO itemDTO : dto.itens()) {
            if (itemDTO == null || itemDTO.produtoId() == null
                    || itemDTO.quantidade() == null || itemDTO.quantidade() <= 0) {
                throw new IllegalArgumentException("Cada item precisa de produto válido e quantidade maior que zero.");
            }
            Long produtoId = itemDTO.produtoId();
            Produto produto = produtos.get(produtoId);
            if (produto == null) {
                produto = produtoRepository.buscarPorId(produtoId).orElseThrow(() ->
                        new IllegalArgumentException("Produto não encontrado: " + produtoId));
                produtos.put(produtoId, produto);
            }
            quantidadesNovas.merge(produtoId, itemDTO.quantidade(), (a, b) -> Math.addExact(a, b));
            BigDecimal preco = produto.getPreco().setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(itemDTO.quantidade()))
                    .setScale(2, RoundingMode.HALF_UP);
            novosItens.add(new ItemComanda(produto.getId(), produto.getNome(),
                    itemDTO.quantidade(), preco, subtotal));
            novoTotal = novoTotal.add(subtotal);
        }

        // Ao editar, o estoque reservado pela comanda atual é considerado disponível novamente.
        for (Map.Entry<Long, Integer> entrada : quantidadesNovas.entrySet()) {
            Produto produto = produtos.get(entrada.getKey());
            int disponivelEfetivo = produto.getEstoque()
                    + quantidadesAntigas.getOrDefault(entrada.getKey(), 0);
            if (disponivelEfetivo < entrada.getValue()) {
                throw new IllegalArgumentException("Estoque insuficiente para '" + produto.getNome()
                        + "'. Disponível considerando esta comanda: " + disponivelEfetivo);
            }
        }
        // Atualiza o estoque para refletir a diferença entre os itens antigos e os novos.
        for (Long produtoId : quantidadesAntigas.keySet()) {
            if (!produtos.containsKey(produtoId)) {
                Produto produto = produtoRepository.buscarPorId(produtoId).orElse(null);
                if (produto != null) produtos.put(produtoId, produto);
            }
        }
        for (Map.Entry<Long, Produto> entrada : produtos.entrySet()) {
            Produto produto = entrada.getValue();
            int antigo = quantidadesAntigas.getOrDefault(entrada.getKey(), 0);
            int novo = quantidadesNovas.getOrDefault(entrada.getKey(), 0);
            produto.setEstoque(produto.getEstoque() + antigo - novo);
            produtoRepository.atualizar(produto);
        }

        existente.setNome(nome);
        existente.setItens(novosItens);
        existente.setTotal(novoTotal.setScale(2, RoundingMode.HALF_UP));
        return converter(comandaRepository.salvar(existente));
    }

    public synchronized ComandaResponseDTO renomear(Long id, String nome) {
        Comanda comanda = obterComanda(id);
        garantirAberta(comanda);
        comanda.setNome(validarNome(nome));
        return converter(comandaRepository.salvar(comanda));
    }

    public synchronized ComandaResponseDTO fechar(Long id) {
        Comanda comanda = obterComanda(id);
        if ("FECHADA".equals(comanda.getStatus())) {
            throw new IllegalArgumentException("Esta comanda já está fechada.");
        }
        comanda.setStatus("FECHADA");
        return converter(comandaRepository.salvar(comanda));
    }

    private Comanda obterComanda(Long id) {
        return comandaRepository.buscarPorId(id).orElseThrow(() ->
                new IllegalArgumentException("Comanda não encontrada: " + id));
    }

    private void garantirAberta(Comanda comanda) {
        if ("FECHADA".equals(comanda.getStatus())) {
            throw new IllegalArgumentException("Não é possível editar uma comanda fechada.");
        }
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe um nome para a comanda.");
        }
        String limpo = nome.trim();
        if (limpo.length() > 80) {
            throw new IllegalArgumentException("O nome da comanda deve ter no máximo 80 caracteres.");
        }
        return limpo;
    }

    public List<ComandaResponseDTO> listar() {
        List<ComandaResponseDTO> respostas = new ArrayList<>();

        for (Comanda comanda : comandaRepository.listarTodas()) {
            respostas.add(converter(comanda));
        }

        return respostas;
    }

    public ComandaResponseDTO buscarPorId(Long id) {
        Comanda comanda = obterComanda(id);

        return converter(comanda);
    }

    private ComandaResponseDTO converter(Comanda comanda) {
        List<ItemComandaResponseDTO> itensDTO = new ArrayList<>();

        for (ItemComanda item : comanda.getItens()) {
            itensDTO.add(new ItemComandaResponseDTO(
                    item.getProdutoId(),
                    item.getNomeProduto(),
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getSubtotal()
            ));
        }

        return new ComandaResponseDTO(
                comanda.getId(),
                comanda.getNome(),
                comanda.getStatus(),
                itensDTO,
                comanda.getTotal(),
                comanda.getDataEmissao()
        );
    }
}
