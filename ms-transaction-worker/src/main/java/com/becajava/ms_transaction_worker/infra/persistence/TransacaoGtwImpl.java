package com.becajava.ms_transaction_worker.infra.persistence;

import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import org.springframework.stereotype.Component;

@Component
public class TransacaoGtwImpl implements TransacaoGateway {
    private final TransacaoRepository repository;

    public TransacaoGtwImpl(TransacaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void atualizar(Transacao transacao) {
        TransacaoEntity entity = new TransacaoEntity(
                transacao.getId(),
                transacao.getPagadorId(),
                transacao.getRecebedorId(),
                transacao.getStatus(),
                transacao.getValor(),
                transacao.getTipo()
        );
        repository.save(entity);
    }
}
