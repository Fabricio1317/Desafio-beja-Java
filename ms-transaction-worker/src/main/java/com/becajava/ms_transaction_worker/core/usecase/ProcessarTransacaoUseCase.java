package com.becajava.ms_transaction_worker.core.usecase;

import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import com.becajava.ms_transaction_worker.infra.dto.UsuarioDTO;
import com.becajava.ms_transaction_worker.infra.integration.MockApiFinanceiroClient;
import com.becajava.ms_transaction_worker.infra.integration.UsuarioClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProcessarTransacaoUseCase {

    private final TransacaoGateway transacaoGateway;
    private final ValidadorGateway validadorGateway;
    private final UsuarioClient usuarioClient;
    private final MockApiFinanceiroClient financeiroClient;

    public ProcessarTransacaoUseCase(
            TransacaoGateway transacaoGateway,
            ValidadorGateway validadorGateway,
            UsuarioClient usuarioClient,
            MockApiFinanceiroClient financeiroClient
    ) {
        this.transacaoGateway = transacaoGateway;
        this.validadorGateway = validadorGateway;
        this.usuarioClient = usuarioClient;
        this.financeiroClient = financeiroClient;
    }

    public void execute(Transacao transacao) {

        System.out.println(" Processando Transação: " + transacao.getId());
        System.out.println(" - Tipo: [" + transacao.getTipo() + "] | Valor: " + transacao.getValor());

        try {

            if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Valor inválido.");
            }


            System.out.println(" Validando usuários no Postgres...");

            UsuarioDTO pagador = null;
            UsuarioDTO recebedor = null;


            if (transacao.getPagadorId() != null) {
                pagador = usuarioClient.buscarPorId(transacao.getPagadorId());

                // Verifica se o CPF não é nulo ANTES de chamar o .length()
                if (pagador.cpf() != null && pagador.cpf().length() == 14 && transacao.getTipo().equalsIgnoreCase("TRANSFERENCIA")) {
                    throw new RuntimeException("Lojistas não podem realizar transferências.");
                }

                if (pagador.cpf() == null) {
                    System.out.println("⚠️ ALERTA: Pagador ID " + pagador.id() + " está sem CPF no cadastro!");
                }
            }


            if (transacao.getRecebedorId() != null) {
                try {
                    recebedor = usuarioClient.buscarPorId(transacao.getRecebedorId());
                } catch (Exception e) {

                    System.out.println("Recebedor não encontrado no Postgres.");
                }
            }


            System.out.println(" Buscando carteiras na MockAPI...");

            ContaExternaDTO carteiraPagador = null;
            ContaExternaDTO carteiraRecebedor = null;

            if (pagador != null) {
                try {
                    carteiraPagador = buscarCarteiraNaMockApi(pagador.id());
                } catch (Exception e) {
                    System.out.println("Carteira do Pagador não encontrada.");
                }
            }

            if (recebedor != null) {
                try {
                    carteiraRecebedor = buscarCarteiraNaMockApi(recebedor.id());
                } catch (Exception e) {
                    System.out.println(" Carteira do Recebedor não encontrada.");
                }
            }


            if (transacao.getTipo().equals("TRANSFERENCIA")) {
                if (carteiraPagador == null) throw new RuntimeException("Pagador não possui carteira ativa.");
                if (carteiraRecebedor == null) throw new RuntimeException("Recebedor não possui carteira ativa.");
            }
            if (transacao.getTipo().equals("DEPOSITO") && carteiraRecebedor == null) {
                throw new RuntimeException("Recebedor não possui carteira para depósito.");
            }
            if (transacao.getTipo().equals("SAQUE") && carteiraPagador == null) {
                throw new RuntimeException("Pagador não possui carteira para saque.");
            }


            if (carteiraPagador != null && (transacao.getTipo().equals("TRANSFERENCIA") || transacao.getTipo().equals("SAQUE"))) {
                if (carteiraPagador.saldo().compareTo(transacao.getValor()) < 0) {
                    throw new RuntimeException("Saldo insuficiente na MockAPI.");
                }
            }


            validarLimiteDolar(transacao);


            System.out.println(" Atualizando saldos na MockAPI...");

            String tipo = transacao.getTipo().toUpperCase();


            if (tipo.equals("TRANSFERENCIA") || tipo.equals("SAQUE")) {
                BigDecimal novoSaldo = carteiraPagador.saldo().subtract(transacao.getValor());

                financeiroClient.atualizarSaldo(carteiraPagador.id(),
                        new ContaExternaDTO(carteiraPagador.id(), carteiraPagador.userId(), novoSaldo));

                System.out.println("   -> Débito efetuado na conta " + carteiraPagador.id());
            }


            if (tipo.equals("TRANSFERENCIA") || tipo.equals("DEPOSITO")) {
                BigDecimal novoSaldo = carteiraRecebedor.saldo().add(transacao.getValor());

                financeiroClient.atualizarSaldo(carteiraRecebedor.id(),
                        new ContaExternaDTO(carteiraRecebedor.id(), carteiraRecebedor.userId(), novoSaldo));

                System.out.println(" Crédito efetuado na conta " + carteiraRecebedor.id());
            }


            System.out.println("Transação concluída! Salvando status APROVADA.");
            transacaoGateway.atualizarStatus(transacao.getId(), "APROVADA");

        } catch (Exception e) {
            System.err.println("ERRO NO PROCESSAMENTO: " + e.getMessage());
            transacaoGateway.atualizarStatus(transacao.getId(), "REPROVADA");
        }
        System.out.println("------------------------------------------------");
    }



    private ContaExternaDTO buscarCarteiraNaMockApi(Long userId) {
        List<ContaExternaDTO> contas = financeiroClient.buscarCarteiraPorUserId(userId);

        if (contas == null || contas.isEmpty()) {
            throw new RuntimeException("Usuário ID " + userId + " não possui carteira ativa na MockAPI!");
        }
        return contas.get(0);
    }

    private void validarLimiteDolar(Transacao transacao) {
        try {
            double cotacao = validadorGateway.obterCotacaoDolar();
            if (cotacao <= 0) cotacao = 5.0;

            BigDecimal limite = BigDecimal.valueOf(1000 * cotacao);
            if (transacao.getValor().compareTo(limite) > 0) {
                throw new RuntimeException("Valor excede o limite regulatório (USD 1000).");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Aviso: Não foi possível validar limite Dólar. Prosseguindo...");
        }
    }
}