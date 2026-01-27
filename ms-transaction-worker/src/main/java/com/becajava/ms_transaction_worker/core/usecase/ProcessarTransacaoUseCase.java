package com.becajava.ms_transaction_worker.core.usecase;

import com.becajava.ms_transaction_worker.core.domain.StatusTransacao;
import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import com.becajava.ms_transaction_worker.infra.integration.MockApiFinanceiroClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProcessarTransacaoUseCase {

    private final TransacaoGateway transacaoGateway;
    private final ValidadorGateway validadorGateway;
    private final MockApiFinanceiroClient financeiroClient;

    public ProcessarTransacaoUseCase(
            TransacaoGateway transacaoGateway,
            ValidadorGateway validadorGateway,
            MockApiFinanceiroClient financeiroClient
    ) {
        this.transacaoGateway = transacaoGateway;
        this.validadorGateway = validadorGateway;
        this.financeiroClient = financeiroClient;
    }

    public void execute(Transacao transacao) {
        System.out.println("Processando: " + transacao.getDescricao() + " | Valor: " + transacao.getValor());

        try {
            if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Valor deve ser positivo.");
            }

            try {
                if (!validadorGateway.usuarioExiste(transacao.getUsuarioId())) {
                    throw new RuntimeException("Usuario nao encontrado no MS-User.");
                }
            } catch (Exception e) {
                System.out.println("Aviso: Nao foi possivel validar usuario. Seguindo...");
            }

            ContaExternaDTO carteira = buscarCarteiraMock(transacao.getUsuarioId());

            if (carteira.getId() == null) {
                throw new RuntimeException("ERRO: Carteira encontrada mas ID veio NULO.");
            }

            validarLimiteDolar(transacao);

            BigDecimal novoSaldo = carteira.getSaldo();
            String tipo = transacao.getTipo() != null ? transacao.getTipo().toUpperCase() : "DESPESA";

            if (tipo.equals("DESPESA") || tipo.equals("SAQUE")) {
                novoSaldo = novoSaldo.subtract(transacao.getValor());
            } else if (tipo.equals("RECEITA") || tipo.equals("DEPOSITO")) {
                novoSaldo = novoSaldo.add(transacao.getValor());
            }

            financeiroClient.atualizarSaldo(carteira.getId(),
                    new ContaExternaDTO(carteira.getId(), carteira.getUserId(), novoSaldo));

            System.out.println("Transacao APROVADA. Novo Saldo: " + novoSaldo);
            transacaoGateway.atualizarStatus(transacao.getId(), StatusTransacao.APROVADA);

        } catch (Exception e) {
            System.err.println("ERRO NO PROCESSAMENTO: " + e.getMessage());
            try {
                transacaoGateway.atualizarStatus(transacao.getId(), StatusTransacao.REPROVADA);
            } catch (Exception exDb) {
                System.err.println("Nao foi possivel salvar status REPROVADA no banco: " + exDb.getMessage());
            }
        }
    }

    private ContaExternaDTO buscarCarteiraMock(Long userId) {
        try {
            List<ContaExternaDTO> contas = financeiroClient.buscarCarteiraPorUserId(userId);
            if (contas == null || contas.isEmpty()) {
                throw new RuntimeException("Usuario " + userId + " sem carteira na MockAPI.");
            }
            return contas.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar carteira: " + e.getMessage());
        }
    }

    private void validarLimiteDolar(Transacao transacao) {
        try {
            double cotacao = validadorGateway.obterCotacaoDolar();
            if (cotacao <= 0) cotacao = 5.0;
            BigDecimal limite = BigDecimal.valueOf(1000 * cotacao);

            if (transacao.getValor().compareTo(limite) > 0) {
                System.out.println("ALERTA: Transacao de alto valor.");
            }
        } catch (Exception e) {
            System.out.println("Aviso: Falha na cotacao, prosseguindo.");
        }
    }
}