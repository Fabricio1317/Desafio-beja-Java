package com.becajava.ms_transaction_worker.infra.persistence;

import com.becajava.ms_transaction_worker.core.domain.StatusTransacao;
import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransacaoGtwImpl implements TransacaoGateway {
    private final TransacaoRepository repository;

    public TransacaoGtwImpl(TransacaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void atualizar(Transacao transacao) {
        TransacaoEntity entity = converterParaEntity(transacao);
        repository.save(entity);
    }

    @Override
    public void atualizarStatus(UUID id, StatusTransacao novoStatus) {
        TransacaoEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada no BD Local"));

        entity.setStatus(novoStatus);
        repository.save(entity);
    }

    private TransacaoEntity converterParaEntity(Transacao t) {
        return new TransacaoEntity(
                t.getId(),
                t.getUsuarioId(),
                t.getStatus(),
                t.getValor(),
                t.getTipo(),
                t.getCategoria(),
                t.getDescricao()
        );
    }
}