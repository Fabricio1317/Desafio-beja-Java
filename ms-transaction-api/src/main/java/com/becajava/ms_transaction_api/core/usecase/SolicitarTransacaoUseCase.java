package com.becajava.ms_transaction_api.core.usecase;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.domain.StatusTransacao;
import com.becajava.ms_transaction_api.core.exception.RegraDeNegocioException; // <--- Import Novo
import com.becajava.ms_transaction_api.core.gateway.MensageriaGateway;
import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_api.core.dto.TransacaoRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SolicitarTransacaoUseCase {

    private final MensageriaGateway mensageriaGateway;
    private final TransacaoGateway transacaoGateway;

    public SolicitarTransacaoUseCase(MensageriaGateway mensageriaGateway, TransacaoGateway transacaoGateway) {
        this.mensageriaGateway = mensageriaGateway;
        this.transacaoGateway = transacaoGateway;
    }

    public void execute(TransacaoRequestDTO dto) {

        if (dto.valor() == null || dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("O valor da transação deve ser maior que zero.");
        }

        System.out.println("1. Recebendo solicitação: " + dto.descricao());

        Transacao transacao = new Transacao();
        transacao.setUsuarioId(dto.usuarioId());
        transacao.setValor(dto.valor());
        transacao.setTipo(dto.tipo());
        transacao.setCategoria(dto.categoria());
        transacao.setDescricao(dto.descricao());
        transacao.setDataCriacao(LocalDateTime.now());

        transacao.setStatus(StatusTransacao.PENDENTE);

        Transacao transacaoSalva = transacaoGateway.salvar(transacao);
        System.out.println("2. Salvo no banco com ID: " + transacaoSalva.getId());

        mensageriaGateway.enviar(transacaoSalva);

        System.out.println("3. Enviado para o Kafka.");
    }
}