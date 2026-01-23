package com.becajava.ms_transaction_worker.core.usecase;

import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import com.becajava.ms_transaction_worker.infra.dto.TransacaoMockDTO;
import com.becajava.ms_transaction_worker.infra.integration.MockApiFinanceiroClient;
import com.becajava.ms_transaction_worker.infra.integration.UsuarioClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProcessarTransacaoUseCase {

    private final TransacaoGateway transacaoGateway;
    private final ValidadorGateway validadorGateway;
    private final UsuarioClient usuarioClient;
    private final MockApiFinanceiroClient mockApiFinanceiroClient;

    public ProcessarTransacaoUseCase(
            TransacaoGateway transacaoGateway,
            ValidadorGateway validadorGateway,
            UsuarioClient usuarioClient,
            MockApiFinanceiroClient mockApiFinanceiroClient
    ) {
        this.transacaoGateway = transacaoGateway;
        this.validadorGateway = validadorGateway;
        this.usuarioClient = usuarioClient;
        this.mockApiFinanceiroClient = mockApiFinanceiroClient;
    }

    public void execute(Transacao transacao) {
        System.out.println("------------------------------------------------");
        System.out.println("DEBUG - Tipo recebido: [" + transacao.getTipo() + "]");
        System.out.println(" Iniciando validações para transação: " + transacao.getId());

        if (transacao.getTipo() == null || transacao.getTipo().isBlank()) {
            System.out.println("  Tipo da transação é obrigatório");
            rejeitarESalvar(transacao);
            return;
        }

        if (transacao.getValor() == null || transacao.getValor().doubleValue() <= 0) {
            System.out.println(" Valor da transação inválido");
            rejeitarESalvar(transacao);
            return;
        }

        String tipo = transacao.getTipo().toUpperCase();

        if (!tipo.equals("TRANSFERENCIA")
                && !tipo.equals("DEPOSITO")
                && !tipo.equals("SAQUE")) {

            System.out.println("  Tipo da transação inválido: " + tipo);
            rejeitarESalvar(transacao);
            return;
        }

        if (tipo.equals("SAQUE") || tipo.equals("TRANSFERENCIA")) {
            System.out.println(" Consultando saldo no Banco Central (MockAPI)...");
            try {
                // Busca a conta externa usando o ID do pagador
                ContaExternaDTO contaExterna = mockApiFinanceiroClient.buscarConta(transacao.getPagadorId());

                System.out.println(" Saldo externo encontrado: " + contaExterna.saldo());

                // Verifica se o saldo externo é menor que o valor da transação
                if (contaExterna.saldo().compareTo(transacao.getValor()) < 0) {
                    System.out.println("  REJEITADA: Saldo insuficiente no Banco Central (MockAPI)!");
                    rejeitarESalvar(transacao);
                    return; // Interrompe o processo aqui
                }

            } catch (Exception e) {
                System.out.println("  Erro crítico: Não foi possível validar saldo no MockAPI: " + e.getMessage());
                // Por segurança, se o banco central tá fora, rejeitamos a transação
                rejeitarESalvar(transacao);
                return;
            }
        }


        var pagador = transacao.getPagadorId() != null
                ? usuarioClient.buscarPorId(transacao.getPagadorId())
                : null;

        var recebedor = transacao.getRecebedorId() != null
                ? usuarioClient.buscarPorId(transacao.getRecebedorId())
                : null;

        boolean pagadorOk =
                tipo.equals("DEPOSITO") || pagador != null;

        boolean recebedorOk =
                tipo.equals("SAQUE") || recebedor != null;

        if (!pagadorOk || !recebedorOk) {
            System.out.println("  Usuário inválido para o tipo da transação");
            rejeitarESalvar(transacao);
            return;
        }

        // ===============================
        // 3️⃣ VALIDA LIMITE EM DÓLAR
        // ===============================
        double dolarHoje;
        try {
            dolarHoje = validadorGateway.obterCotacaoDolar();
            if (dolarHoje <= 0) dolarHoje = 5.0;
        } catch (Exception e) {
            // Log limpo, sem estourar erro gigante
            System.out.println("  Dólar indisponível, usando cotação padrão: 5.0");
            dolarHoje = 5.0;
        }

        double limite = 1000 * dolarHoje;

        if (transacao.getValor().doubleValue() > limite) {
            System.out.println("  Valor excede limite permitido");
            rejeitarESalvar(transacao);
            return;
        }

        // ===============================
        // 4️⃣ APROVA E EXECUTA (BANCO LOCAL)
        // ===============================
        transacao.aprovar();

        switch (tipo) {
            case "TRANSFERENCIA" -> {
                usuarioClient.atualizarSaldo(
                        pagador.id(),
                        pagador.saldo().subtract(transacao.getValor())
                );
                usuarioClient.atualizarSaldo(
                        recebedor.id(),
                        recebedor.saldo().add(transacao.getValor())
                );
            }
            case "DEPOSITO" -> {
                usuarioClient.atualizarSaldo(
                        recebedor.id(),
                        recebedor.saldo().add(transacao.getValor())
                );
            }
            case "SAQUE" -> {
                usuarioClient.atualizarSaldo(
                        pagador.id(),
                        pagador.saldo().subtract(transacao.getValor())
                );
            }
        }

        // =============================================================
        // 🆕 ATUALIZAÇÃO DO SALDO NO BANCO CENTRAL (MOCK API) - CORRIGIDO
        // =============================================================
        try {
            // 🟥 1. LADO DO PAGADOR (Quem perde dinheiro: SAQUE ou TRANSFERENCIA)
            if (tipo.equals("SAQUE") || tipo.equals("TRANSFERENCIA")) {
                Long idPagador = transacao.getPagadorId();

                // Busca conta e Subtrai
                ContaExternaDTO contaPagador = mockApiFinanceiroClient.buscarConta(idPagador);
                BigDecimal novoSaldoPagador = contaPagador.saldo().subtract(transacao.getValor());

                // Atualiza no Mock
                mockApiFinanceiroClient.atualizarSaldoExterno(
                        idPagador,
                        new ContaExternaDTO(idPagador, novoSaldoPagador)
                );
                System.out.println(" Débito realizado no MockAPI (ID " + idPagador + "): " + novoSaldoPagador);
            }

            // 🟩 2. LADO DO RECEBEDOR (Quem ganha dinheiro: DEPOSITO ou TRANSFERENCIA)
            if (tipo.equals("DEPOSITO") || tipo.equals("TRANSFERENCIA")) {
                Long idRecebedor = transacao.getRecebedorId();

                // Busca conta e Soma
                ContaExternaDTO contaRecebedor = mockApiFinanceiroClient.buscarConta(idRecebedor);
                BigDecimal novoSaldoRecebedor = contaRecebedor.saldo().add(transacao.getValor());

                // Atualiza no Mock
                mockApiFinanceiroClient.atualizarSaldoExterno(
                        idRecebedor,
                        new ContaExternaDTO(idRecebedor, novoSaldoRecebedor)
                );
                System.out.println("  Crédito realizado no MockAPI (ID " + idRecebedor + "): " + novoSaldoRecebedor);
            }

        } catch (Exception e) {
            System.out.println("  ALERTA: Erro ao atualizar MockAPI: " + e.getMessage());
            // Logamos o erro mas não paramos, pois o banco local já foi atualizado
        }

        // ===============================
        // 5️⃣ SALVA HISTÓRICO (Mock API)
        // ===============================
        TransacaoMockDTO historico = new TransacaoMockDTO(
                transacao.getPagadorId(),
                transacao.getRecebedorId(),
                transacao.getValor().doubleValue(),
                tipo,
                LocalDateTime.now().toString()
        );

        // Envolvemos em try-catch para garantir que falha de log não quebre o processo
        try {
            mockApiFinanceiroClient.salvarNoMock(historico);
        } catch (Exception e) {
            System.out.println("  Falha ao salvar histórico, mas transação foi concluída.");
        }

        System.out.println("  Transação " + tipo + " APROVADA com sucesso!");
        System.out.println("------------------------------------------------");

        transacaoGateway.atualizar(transacao);
    }

    private void rejeitarESalvar(Transacao transacao) {
        transacao.rejeitar();
        transacaoGateway.atualizar(transacao);
        System.out.println("------------------------------------------------");
    }
}