package com.becajava.ms_transaction_api.infra.persistence;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class GtwImpl implements TransacaoGateway {

    private static final Logger log = LoggerFactory.getLogger(GtwImpl.class);
    private final TransacaoRepository repository;

    public GtwImpl(TransacaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transacao salvar(Transacao transacao) {
        try {
            TransacaoEntity entity = new TransacaoEntity(
                    transacao.getId(),
                    transacao.getUsuarioId(),
                    transacao.getStatus(),
                    transacao.getValor(),
                    transacao.getTipo(),
                    transacao.getCategoria(),
                    transacao.getDescricao()
            );

            TransacaoEntity salvo = repository.save(entity);

            transacao.setId(salvo.getId());
            transacao.setDataCriacao(salvo.getDataCriacao());
            transacao.setStatus(salvo.getStatus());

            log.info(" Transação salva no banco de dados com ID: {}", salvo.getId());
            return transacao;

        } catch (Exception e) {
            log.error(" Erro ao salvar transação no banco: {}", e.getMessage());
            throw new RuntimeException("Erro técnico ao salvar transação no banco de dados.", e);
        }
    }

    @Override
    public Optional<Transacao> buscaPorId(UUID id) {
        try {
            return repository.findById(id).map(e -> new Transacao(
                    e.getId(),
                    e.getUsuarioId(),
                    e.getValor(),
                    e.getTipo(),
                    e.getCategoria(),
                    e.getDescricao(),
                    e.getStatus(),
                    e.getDataCriacao()
            ));
        } catch (Exception e) {
            log.error(" Erro ao buscar transação por ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Erro técnico ao buscar transação.", e);
        }
    }

    @Override
    public List<Transacao> buscarPorUsuarioId(Long usuarioId) {
        try {
            List<Transacao> lista = repository.findAllByUsuarioId(usuarioId)
                    .stream()
                    .map(e -> new Transacao(
                            e.getId(),
                            e.getUsuarioId(),
                            e.getValor(),
                            e.getTipo(),
                            e.getCategoria(),
                            e.getDescricao(),
                            e.getStatus(),
                            e.getDataCriacao()
                    ))
                    .collect(Collectors.toList());

            log.info(" Buscando transações do usuário {}. Encontradas: {}", usuarioId, lista.size());
            return lista;

        } catch (Exception e) {
            log.error(" Erro ao listar transações do usuário {}: {}", usuarioId, e.getMessage());
            throw new RuntimeException("Erro técnico ao gerar extrato do banco de dados.", e);
        }
    }
}