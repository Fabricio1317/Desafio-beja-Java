package com.becajava.ms_transaction_api.infra.persistence;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class GtwImpl implements TransacaoGateway {

    private final TransacaoRepository repository;

    public GtwImpl(TransacaoRepository repository) {
        this.repository = repository;
    }


    @Override
    public Transacao salvar(Transacao transacao) {
        TransacaoEntity entity = new TransacaoEntity(
                transacao.getId(),
                transacao.getPagadorId(),
                transacao.getRecebedorId(),
                transacao.getValor(),
                transacao.getStatus(),
                transacao.getDataCriacao(),
                transacao.getTipo());
        repository.save(entity);
        return transacao;
    }

    @Override
    public Optional<Transacao> buscaPorId(UUID id) {
        return repository.findById(id).map(e->  new Transacao(
                e.getId(),
                e.getPagadorId(),
                e.getRecebedorId(),
                e.getValor(),
                e.getStatus(),
                e.getDataCriacao(),
                e.getTipo())
        );
    }
}
