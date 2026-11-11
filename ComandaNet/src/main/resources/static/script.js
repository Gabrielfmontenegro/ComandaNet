
/* =========================================
   COMANDANET — SCRIPT PRINCIPAL
   Integração com Java Spring Boot
   ========================================= */

const API = {
    produtos: "/produtos",
    comandas: "/comandas"
};

const $ = (seletor) => document.querySelector(seletor);

const state = {
    produtos: [],
    comandas: [],
    itensComanda: [],
    editingComandaId: null,
    toastTimer: null
};

/* =========================================
   REQUISIÇÕES À API
   ========================================= */

async function requisicao(url, opcoes = {}) {
    const resposta = await fetch(url, {
        ...opcoes,
        headers: {
            "Content-Type": "application/json",
            ...(opcoes.headers || {})
        }
    });

    if (!resposta.ok) {
        let mensagem = `Erro HTTP ${resposta.status}`;

        try {
            const erro = await resposta.json();
            mensagem = erro.message
                || erro.mensagem
                || erro.detail
                || mensagem;
        } catch {
            // Mantém a mensagem HTTP original.
        }

        throw new Error(mensagem);
    }

    if (resposta.status === 204) {
        return null;
    }

    const texto = await resposta.text();
    return texto ? JSON.parse(texto) : null;
}

/* =========================================
   FORMATAÇÃO E MENSAGENS
   ========================================= */

function escapar(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, caractere => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[caractere]);
}

function moeda(valor) {
    return Number(valor || 0).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

function dataFormatada(valor) {
    if (!valor) return "—";

    const data = new Date(valor);

    if (Number.isNaN(data.getTime())) {
        return escapar(valor);
    }

    return data.toLocaleString("pt-BR");
}

function mostrarToast(mensagem, tipo = "success") {
    const toast = $("#toast");
    if (!toast) return;

    clearTimeout(state.toastTimer);

    toast.textContent = mensagem;
    toast.className = `toast ${tipo}`;
    toast.hidden = false;

    state.toastTimer = setTimeout(() => {
        toast.hidden = true;
    }, 4000);
}

function mostrarMensagem(seletor, mensagem, tipo = "success") {
    const elemento = $(seletor);
    if (!elemento) return;

    elemento.textContent = mensagem;
    elemento.className = `form-message ${tipo}`;
}

function atualizarContadorDashboard(id, quantidade) {
    const elemento = document.getElementById(id);

    if (elemento) {
        elemento.textContent = quantidade;
    }
}

/* =========================================
   NAVEGAÇÃO
   ========================================= */

function navegar(pagina) {
    const paginasValidas = ["inicio", "produtos", "comandas"];

    if (!paginasValidas.includes(pagina)) return;

    document.querySelectorAll(".page").forEach(secao => {
        secao.hidden = secao.id !== pagina;
    });

    document.querySelectorAll(".nav-link").forEach(botao => {
        const ativo = botao.dataset.page === pagina;

        botao.classList.toggle("active", ativo);

        if (ativo) {
            botao.setAttribute("aria-current", "page");
        } else {
            botao.removeAttribute("aria-current");
        }
    });

    const titulos = {
        inicio: "Dashboard",
        produtos: "Produtos",
        comandas: "Comandas"
    };

    const subtitulos = {
        inicio: "Acompanhe os dados do seu sistema.",
        produtos: "Cadastre produtos e controle o estoque.",
        comandas: "Emita e consulte comandas."
    };

    if ($("#page-title")) {
        $("#page-title").textContent = titulos[pagina];
    }

    if ($("#page-subtitle")) {
        $("#page-subtitle").textContent = subtitulos[pagina];
    }

    if (pagina === "inicio") {
        atualizarDashboard();
    }

    if (pagina === "produtos") {
        carregarProdutos();
    }

    if (pagina === "comandas") {
        carregarProdutos();
        carregarComandas();
    }

    const sidebar = $("#sidebar");
    if (sidebar) {
        sidebar.classList.remove("open");
    }
}

function configurarNavegacao() {
    document.querySelectorAll("[data-page]").forEach(botao => {
        botao.addEventListener("click", () => {
            navegar(botao.dataset.page);
        });
    });

    $("#mobile-menu")?.addEventListener("click", () => {
        $("#sidebar")?.classList.toggle("open");
    });
}

/* =========================================
   DASHBOARD
   ========================================= */

async function atualizarDashboard() {
    const botao = $("#refresh-dashboard");

    if (botao) botao.disabled = true;

    try {
        const [produtos, comandas] = await Promise.all([
            requisicao(API.produtos),
            requisicao(API.comandas)
        ]);

        state.produtos = Array.isArray(produtos) ? produtos : [];
        state.comandas = Array.isArray(comandas) ? comandas : [];

        atualizarContadorDashboard(
            "total-produtos",
            state.produtos.length
        );

        atualizarContadorDashboard(
            "total-comandas",
            state.comandas.length
        );

        atualizarContadores();
        preencherProdutosComanda();
        renderizarItensComanda();
    } catch (erro) {
        mostrarToast(
            `Não foi possível atualizar o dashboard: ${erro.message}`,
            "error"
        );
    } finally {
        if (botao) botao.disabled = false;
    }
}

/* =========================================
   PRODUTOS
   ========================================= */

async function carregarProdutos() {
    const tabela = $("#tabela-produtos");

    try {
        const dados = await requisicao(API.produtos);

        state.produtos = Array.isArray(dados) ? dados : [];

        if (tabela) {
            if (state.produtos.length === 0) {
                tabela.innerHTML = `
                    <tr>
                        <td colspan="5" class="empty-cell">
                            Nenhum produto cadastrado.
                        </td>
                    </tr>
                `;
            } else {
                tabela.innerHTML = state.produtos.map(produto => `
                    <tr>
                        <td>${escapar(produto.id)}</td>
                        <td>${escapar(produto.nome)}</td>
                        <td>${moeda(produto.preco)}</td>
                        <td>${escapar(produto.estoque)}</td>
                        <td>${escapar(produto.descricao || "—")}</td>
                    </tr>
                `).join("");
            }
        }

        atualizarContadores();
        preencherProdutosComanda();
        renderizarItensComanda();
    } catch (erro) {
        if (tabela) {
            tabela.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-cell">
                        Não foi possível carregar os produtos.
                    </td>
                </tr>
            `;
        }

        mostrarToast(
            `Erro ao carregar produtos: ${erro.message}`,
            "error"
        );
    }
}

async function cadastrarProduto(evento) {
    evento.preventDefault();

    const formulario = evento.currentTarget;
    const botao = formulario.querySelector('button[type="submit"]');
    const dados = new FormData(formulario);

    const produto = {
        nome: String(dados.get("nome") || "").trim(),
        descricao: String(dados.get("descricao") || "").trim(),
        preco: Number(dados.get("preco")),
        estoque: Number(dados.get("estoque"))
    };

    if (!produto.nome) {
        mostrarMensagem(
            "#mensagem-produto",
            "Informe o nome do produto.",
            "error"
        );
        return;
    }

    if (!Number.isFinite(produto.preco) || produto.preco <= 0) {
        mostrarMensagem(
            "#mensagem-produto",
            "O preço deve ser maior que zero.",
            "error"
        );
        return;
    }

    if (!Number.isInteger(produto.estoque) || produto.estoque < 0) {
        mostrarMensagem(
            "#mensagem-produto",
            "O estoque deve ser um número inteiro igual ou maior que zero.",
            "error"
        );
        return;
    }

    botao.disabled = true;

    try {
        await requisicao(API.produtos, {
            method: "POST",
            body: JSON.stringify(produto)
        });

        formulario.reset();

        mostrarMensagem(
            "#mensagem-produto",
            "Produto cadastrado com sucesso!",
            "success"
        );

        mostrarToast("Produto cadastrado com sucesso!");

        await carregarProdutos();
        await atualizarDashboard();
    } catch (erro) {
        mostrarMensagem(
            "#mensagem-produto",
            `Não foi possível cadastrar: ${erro.message}`,
            "error"
        );
    } finally {
        botao.disabled = false;
    }
}

function atualizarContadores() {
    const contadorProdutos = $("#contador-produtos");
    const contadorComandas = $("#contador-comandas");

    if (contadorProdutos) {
        contadorProdutos.textContent =
            `${state.produtos.length} produto(s)`;
    }

    if (contadorComandas) {
        contadorComandas.textContent =
            `${state.comandas.length} comanda(s)`;
    }

    atualizarContadorDashboard(
        "total-produtos",
        state.produtos.length
    );

    atualizarContadorDashboard(
        "total-comandas",
        state.comandas.length
    );
}

/* =========================================
   PRODUTOS DISPONÍVEIS PARA A NOTA
   ========================================= */

function preencherProdutosComanda() {
    const campo = $("#comanda-produto");
    if (!campo) return;

    const selecionado = campo.value;

    campo.innerHTML = `
        <option value="">Selecione um produto</option>
        ${state.produtos.map(produto => `
            <option value="${escapar(produto.id)}"
                    ${Number(produto.estoque) <= 0 ? "disabled" : ""}>
                ${escapar(produto.nome)} — ${moeda(produto.preco)}
                (estoque: ${escapar(produto.estoque)})
            </option>
        `).join("")}
    `;

    if (
        state.produtos.some(
            produto => String(produto.id) === selecionado
        )
    ) {
        campo.value = selecionado;
    }
}

/* =========================================
   ITENS DA COMANDA
   ========================================= */

function obterProduto(produtoId) {
    return state.produtos.find(
        produto => Number(produto.id) === Number(produtoId)
    );
}

function quantidadeNoRascunho(produtoId) {
    return state.itensComanda
        .filter(item => Number(item.produtoId) === Number(produtoId))
        .reduce((soma, item) => soma + item.quantidade, 0);
}

function adicionarItemComanda() {
    const produtoId = Number($("#comanda-produto")?.value);
    const quantidade = Number($("#comanda-quantidade")?.value);

    if (!produtoId) {
        mostrarMensagem(
            "#mensagem-comanda",
            "Selecione um produto.",
            "error"
        );
        return;
    }

    if (!Number.isInteger(quantidade) || quantidade < 1) {
        mostrarMensagem(
            "#mensagem-comanda",
            "A quantidade deve ser um número inteiro maior que zero.",
            "error"
        );
        return;
    }

    const produto = obterProduto(produtoId);

    if (!produto) {
        mostrarMensagem(
            "#mensagem-comanda",
            "O produto selecionado não foi encontrado. Atualize a lista.",
            "error"
        );
        return;
    }

    const quantidadeAtual = quantidadeNoRascunho(produtoId);
    const quantidadeTotal = quantidadeAtual + quantidade;
    const comandaEmEdicao = state.editingComandaId == null
        ? null
        : state.comandas.find(c => String(c.id) === String(state.editingComandaId));
    const quantidadeReservada = (comandaEmEdicao?.itens || [])
        .filter(i => Number(i.produtoId) === produtoId)
        .reduce((total, i) => total + Number(i.quantidade || 0), 0);
    const estoque = Number(produto.estoque) + quantidadeReservada;

    if (quantidadeTotal > estoque) {
        mostrarMensagem(
            "#mensagem-comanda",
            `Estoque insuficiente para ${produto.nome}. ` +
            `Disponível: ${Math.max(0, estoque - quantidadeAtual)}.`,
            "error"
        );
        return;
    }

    const existente = state.itensComanda.find(
        item => Number(item.produtoId) === produtoId
    );

    if (existente) {
        existente.quantidade += quantidade;
    } else {
        state.itensComanda.push({
            produtoId: produto.id,
            quantidade
        });
    }

    renderizarItensComanda();

    $("#comanda-quantidade").value = "1";
    $("#comanda-produto").value = "";

    mostrarMensagem(
        "#mensagem-comanda",
        "Produto adicionado à comanda.",
        "success"
    );
}

function removerItemComanda(produtoId) {
    state.itensComanda = state.itensComanda.filter(
        item => Number(item.produtoId) !== Number(produtoId)
    );

    renderizarItensComanda();

    mostrarMensagem(
        "#mensagem-comanda",
        "Produto removido da comanda.",
        "success"
    );
}

function renderizarItensComanda() {
    const tabela = $("#itens-comanda");
    const totalElemento = $("#total-previsto");

    if (!tabela) return;

    if (state.itensComanda.length === 0) {
        tabela.innerHTML = `
            <tr>
                <td colspan="5" class="empty-cell">
                    Nenhum produto adicionado.
                </td>
            </tr>
        `;

        if (totalElemento) {
            totalElemento.textContent = moeda(0);
        }

        return;
    }

    let total = 0;

    tabela.innerHTML = state.itensComanda.map(item => {
        const produto = obterProduto(item.produtoId);

        if (!produto) {
            return `
                <tr>
                    <td colspan="5" class="empty-cell">
                        Produto ${escapar(item.produtoId)} não encontrado.
                        Remova-o e adicione novamente.
                    </td>
                </tr>
            `;
        }

        const preco = Number(produto.preco);
        const subtotal = preco * item.quantidade;

        total += subtotal;

        return `
            <tr>
                <td>${escapar(produto.nome)}</td>
                <td>${escapar(item.quantidade)}</td>
                <td>${moeda(preco)}</td>
                <td>${moeda(subtotal)}</td>
                <td>
                    <button
                        class="table-action"
                        type="button"
                        data-remover-produto="${escapar(item.produtoId)}">
                        Remover
                    </button>
                </td>
            </tr>
        `;
    }).join("");

    if (totalElemento) {
        totalElemento.textContent = moeda(total);
    }
}

/* =========================================
   EMISSÃO DE COMANDA
   ========================================= */

async function emitirComanda(evento) {
    evento.preventDefault();

    const formulario = evento.currentTarget;
    const botao = formulario.querySelector('button[type="submit"]');

    if (state.itensComanda.length === 0) {
        mostrarMensagem(
            "#mensagem-comanda",
            "Adicione pelo menos um produto antes de emitir a comanda.",
            "error"
        );
        return;
    }

    // Confere novamente o estoque antes de enviar a solicitação.
    for (const item of state.itensComanda) {
        const produto = obterProduto(item.produtoId);

        if (!produto) {
            mostrarMensagem(
                "#mensagem-comanda",
                `O produto ${item.produtoId} não está disponível. Atualize os produtos.`,
                "error"
            );
            return;
        }

        const comandaEmEdicao = state.editingComandaId == null
            ? null
            : state.comandas.find(c => String(c.id) === String(state.editingComandaId));
        const quantidadeAnterior = (comandaEmEdicao?.itens || [])
            .filter(i => Number(i.produtoId) === Number(item.produtoId))
            .reduce((total, i) => total + Number(i.quantidade || 0), 0);
        if (item.quantidade > Number(produto.estoque) + quantidadeAnterior) {
            mostrarMensagem(
                "#mensagem-comanda",
                `Estoque insuficiente para ${produto.nome}. Atualize os produtos e confira a quantidade.`,
                "error"
            );

            await carregarProdutos();
            return;
        }
    }

    // O backend espera uma lista de itens; a comanda não possui cadastro de cliente.
    const nomeComanda = $("#comanda-nome")?.value.trim();
    if (!nomeComanda) {
        mostrarMensagem("#mensagem-comanda", "Informe um nome para a comanda.", "error");
        return;
    }

    const corpo = {
        nome: nomeComanda,
        itens: state.itensComanda.map(item => ({
            produtoId: Number(item.produtoId),
            quantidade: Number(item.quantidade)
        }))
    };

    botao.disabled = true;

    try {
        const editando = state.editingComandaId !== null;
        const url = editando
            ? `${API.comandas}/${encodeURIComponent(state.editingComandaId)}`
            : API.comandas;
        const comanda = await requisicao(url, {
            method: editando ? "PUT" : "POST",
            body: JSON.stringify(corpo)
        });
        state.editingComandaId = null;

        state.itensComanda = [];
        renderizarItensComanda();

        formulario.reset();
        $("#comanda-quantidade").value = "1";
        const submit = formulario.querySelector('button[type="submit"]');
        if (submit) submit.textContent = "✦ Emitir comanda";

        mostrarMensagem(
            "#mensagem-comanda",
            `${editando ? "Comanda atualizada" : "Comanda emitida"} com sucesso! Número: ${comanda?.id ?? "—"}`,
            "success"
        );

        mostrarToast(`${editando ? "Comanda atualizada" : "Comanda emitida"} com sucesso!`);

        await Promise.all([
            carregarProdutos(),
            carregarComandas(),
            atualizarDashboard()
        ]);

        if (comanda?.id != null) {
            await verDetalhesComanda(comanda.id);
        }
    } catch (erro) {
        mostrarMensagem(
            "#mensagem-comanda",
            `Não foi possível emitir a comanda: ${erro.message}`,
            "error"
        );

        mostrarToast(
            `Erro ao emitir comanda: ${erro.message}`,
            "error"
        );

        // Atualiza os dados, pois o estoque pode ter mudado.
        await carregarProdutos();
    } finally {
        botao.disabled = false;
    }
}

/* =========================================
   LISTA DE COMANDAS
   ========================================= */

async function carregarComandas() {
    const tabela = $("#tabela-comandas");

    try {
        const dados = await requisicao(API.comandas);
        state.comandas = Array.isArray(dados) ? dados : [];

        if (tabela) {
            if (state.comandas.length === 0) {
                tabela.innerHTML = `
                    <tr>
                        <td colspan="7" class="empty-cell">
                            Nenhuma comanda emitida.
                        </td>
                    </tr>
                `;
            } else {
                tabela.innerHTML = state.comandas.map(comanda => {
                    const itens = Array.isArray(comanda.itens)
                        ? comanda.itens
                        : [];

                    const quantidadeItens = itens.reduce(
                        (soma, item) => soma + Number(item.quantidade || 0),
                        0
                    );

                    const fechada = comanda.status === "FECHADA";
                    return `
                        <tr>
                            <td>${escapar(comanda.id)}</td>
                            <td>${escapar(comanda.nome || `Comanda ${comanda.id}`)}</td>
                            <td><span class="status-badge ${fechada ? "status-closed" : "status-open"}">${fechada ? "Fechada" : "Aberta"}</span></td>
                            <td>${dataFormatada(comanda.dataEmissao)}</td>
                            <td>${quantidadeItens}</td>
                            <td>${moeda(comanda.total)}</td>
                            <td class="comanda-actions">
                                <button class="table-action" type="button" data-comanda-id="${escapar(comanda.id)}">Detalhes</button>
                                ${!fechada ? `<button class="table-action somente-adm" type="button" data-editar-comanda="${escapar(comanda.id)}">Editar</button>
                                <button class="table-action somente-adm" type="button" data-renomear-comanda="${escapar(comanda.id)}">Renomear</button>
                                <button class="table-action somente-adm" type="button" data-fechar-comanda="${escapar(comanda.id)}">Fechar</button>` : ""}
                            </td>
                        </tr>
                    `;
                }).join("");
            }
        }

        document.querySelectorAll(".somente-adm").forEach(elemento => {
            elemento.hidden = window.perfilAtual !== "ADM";
        });
        atualizarContadores();
    } catch (erro) {
        if (tabela) {
            tabela.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-cell">
                        Não foi possível carregar as comandas.
                    </td>
                </tr>
            `;
        }

        mostrarToast(
            `Erro ao carregar comandas: ${erro.message}`,
            "error"
        );
    }
}

async function iniciarEdicaoComanda(id) {
    const comanda = await requisicao(`${API.comandas}/${encodeURIComponent(id)}`);
    if (comanda.status === "FECHADA") throw new Error("Comandas fechadas não podem ser editadas.");
    state.editingComandaId = Number(id);
    $("#comanda-nome").value = comanda.nome || "";
    state.itensComanda = (comanda.itens || []).map(item => ({
        produtoId: Number(item.produtoId),
        nome: item.nomeProduto,
        quantidade: Number(item.quantidade),
        preco: Number(item.precoUnitario),
        subtotal: Number(item.subtotal)
    }));
    renderizarItensComanda();
    const submit = $("#form-comanda")?.querySelector('button[type="submit"]');
    if (submit) submit.textContent = "Salvar alterações";
    mostrarMensagem("#mensagem-comanda", `Editando a comanda ${comanda.nome}. Altere os itens e salve.`, "success");
    $("#form-comanda")?.scrollIntoView({ behavior: "smooth", block: "start" });
}

async function renomearComanda(id) {
    const comanda = state.comandas.find(c => String(c.id) === String(id));
    const nome = prompt("Novo nome da comanda:", comanda?.nome || `Comanda ${id}`);
    if (nome === null) return;
    await requisicao(`${API.comandas}/${encodeURIComponent(id)}/nome`, {
        method: "PATCH",
        body: JSON.stringify({ nome: nome.trim() })
    });
    await carregarComandas();
    await verDetalhesComanda(id);
    mostrarToast("Nome da comanda atualizado.");
}

async function fecharComanda(id) {
    const comanda = state.comandas.find(c => String(c.id) === String(id));
    if (!confirm(`Deseja fechar a comanda "${comanda?.nome || id}"? Depois de fechada, ela não poderá ser editada.`)) return;
    await requisicao(`${API.comandas}/${encodeURIComponent(id)}/fechar`, { method: "POST" });
    await carregarComandas();
    await verDetalhesComanda(id);
    mostrarToast("Comanda fechada com sucesso.");
}

/* =========================================
   DETALHES DA COMANDA
   ========================================= */

async function verDetalhesComanda(id) {
    try {
        const comanda = await requisicao(
            `${API.comandas}/${encodeURIComponent(id)}`
        );

        const painel = $("#detalhes-comanda");
        const titulo = $("#detalhes-titulo");
        const conteudo = $("#detalhes-conteudo");

        if (!painel || !conteudo) return;

        if (titulo) {
            titulo.textContent = `${comanda.nome || `Comanda ${comanda.id}`} (#${comanda.id})`;
        }

        const itens = Array.isArray(comanda.itens) ? comanda.itens : [];

        conteudo.innerHTML = `
            <div class="details-grid">
                <div class="detail-box">
                    <small>Nome da comanda</small>
                    <strong>${escapar(comanda.nome || `Comanda ${comanda.id}`)}</strong>
                </div>
                <div class="detail-box">
                    <small>Status</small>
                    <strong>${comanda.status === "FECHADA" ? "Fechada" : "Aberta"}</strong>
                </div>
                <div class="detail-box">
                    <small>Número da comanda</small>
                    <strong>${escapar(comanda.id)}</strong>
                </div>

                <div class="detail-box">
                    <small>Data de emissão</small>
                    <strong>${dataFormatada(comanda.dataEmissao)}</strong>
                </div>

                <div class="detail-box">
                    <small>Quantidade de itens</small>
                    <strong>${itens.reduce(
                        (soma, item) => soma + Number(item.quantidade || 0),
                        0
                    )}</strong>
                </div>

                <div class="detail-box">
                    <small>Total da comanda</small>
                    <strong>${moeda(comanda.total)}</strong>
                </div>
            </div>

            <div class="section-heading">
                <h2>Itens da comanda</h2>
            </div>

            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Produto</th>
                            <th>Quantidade</th>
                            <th>Preço unitário</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>

                    <tbody>
                        ${
                            itens.length
                                ? itens.map(item => `
                                    <tr>
                                        <td>${escapar(
                                            item.nomeProduto || item.produtoId || "—"
                                        )}</td>
                                        <td>${escapar(item.quantidade ?? "—")}</td>
                                        <td>${moeda(item.precoUnitario)}</td>
                                        <td>${moeda(item.subtotal)}</td>
                                    </tr>
                                `).join("")
                                : `
                                    <tr>
                                        <td colspan="4" class="empty-cell">
                                            Nenhum item encontrado.
                                        </td>
                                    </tr>
                                `
                        }
                    </tbody>
                </table>
            </div>
        `;

        painel.hidden = false;
        painel.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    } catch (erro) {
        mostrarToast(
            `Não foi possível consultar a comanda: ${erro.message}`,
            "error"
        );
    }
}

/* =========================================
   EVENTOS
   ========================================= */

function configurarEventos() {
    $("#form-produto")?.addEventListener(
        "submit",
        cadastrarProduto
    );

    $("#form-comanda")?.addEventListener(
        "submit",
        emitirComanda
    );

    $("#adicionar-item")?.addEventListener(
        "click",
        adicionarItemComanda
    );

    $("#itens-comanda")?.addEventListener("click", evento => {
        const botao = evento.target.closest("[data-remover-produto]");

        if (botao) {
            removerItemComanda(botao.dataset.removerProduto);
        }
    });

    $("#refresh-dashboard")?.addEventListener(
        "click",
        atualizarDashboard
    );

    $("#refresh-products")?.addEventListener(
        "click",
        carregarProdutos
    );

    $("#refresh-comandas")?.addEventListener(
        "click",
        carregarComandas
    );

    $("#close-details")?.addEventListener("click", () => {
        const painel = $("#detalhes-comanda");

        if (painel) {
            painel.hidden = true;
        }
    });

    $("#tabela-comandas")?.addEventListener("click", async evento => {
        const detalhes = evento.target.closest("[data-comanda-id]");
        const editar = evento.target.closest("[data-editar-comanda]");
        const renomear = evento.target.closest("[data-renomear-comanda]");
        const fechar = evento.target.closest("[data-fechar-comanda]");
        try {
            if (editar) await iniciarEdicaoComanda(editar.dataset.editarComanda);
            else if (renomear) await renomearComanda(renomear.dataset.renomearComanda);
            else if (fechar) await fecharComanda(fechar.dataset.fecharComanda);
            else if (detalhes) await verDetalhesComanda(detalhes.dataset.comandaId);
        } catch (erro) {
            mostrarToast(erro.message, "error");
        }
    });
}

/* =========================================
   INICIALIZAÇÃO
   ========================================= */

async function mostrarLogin() {
    const login = $("#tela-login");
    const app = $("#aplicacao");
    if (login) login.hidden = false;
    if (app) app.hidden = true;
}

let eventosDaAplicacaoConfigurados = false;

async function entrarNoSistema(usuario) {
    window.perfilAtual = usuario.perfil;
    const login = $("#tela-login");
    const app = $("#aplicacao");
    if (login) login.hidden = true;
    if (app) app.hidden = false;

    const perfil = usuario.perfil === "ADM" ? "Administrador (ADM)" : "Usuário comum (UC)";
    const nome = $("#nome-usuario");
    const perfilElemento = $("#perfil-usuario");
    const avatar = $("#avatar-usuario");
    if (nome) nome.textContent = usuario.username;
    if (perfilElemento) perfilElemento.textContent = perfil;
    if (avatar) avatar.textContent = (usuario.username || "U").charAt(0).toUpperCase();

    document.querySelectorAll(".somente-adm").forEach(elemento => {
        elemento.hidden = usuario.perfil !== "ADM";
    });

    if (!eventosDaAplicacaoConfigurados) {
        configurarNavegacao();
        configurarEventos();
        eventosDaAplicacaoConfigurados = true;
    }
    renderizarItensComanda();
    navegar("inicio");
    await Promise.allSettled([carregarProdutos(), carregarComandas(), atualizarDashboard()]);
}

async function verificarSessao() {
    try {
        const usuario = await requisicao("/auth/me");
        await entrarNoSistema(usuario);
    } catch {
        await mostrarLogin();
    }
}

document.addEventListener("DOMContentLoaded", () => {
    $("#form-login")?.addEventListener("submit", async evento => {
        evento.preventDefault();
        const mensagem = $("#mensagem-login");
        const botao = evento.currentTarget.querySelector('button[type="submit"]');
        if (botao) botao.disabled = true;
        if (mensagem) mensagem.textContent = "";
        try {
            const usuario = await requisicao("/auth/login", {
                method: "POST",
                body: JSON.stringify({
                    username: $("#login-usuario").value.trim(),
                    senha: $("#login-senha").value
                })
            });
            evento.currentTarget.reset();
            await entrarNoSistema(usuario);
        } catch (erro) {
            if (mensagem) mensagem.textContent = erro.message;
        } finally {
            if (botao) botao.disabled = false;
        }
    });

    $("#btn-logout")?.addEventListener("click", async () => {
        try { await requisicao("/auth/logout", { method: "POST" }); }
        finally { await mostrarLogin(); }
    });

    verificarSessao();
});
