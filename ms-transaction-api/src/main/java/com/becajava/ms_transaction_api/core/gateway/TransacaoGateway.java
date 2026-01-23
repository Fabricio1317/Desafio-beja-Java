package com.becajava.ms_transaction_api.core.gateway;

import com.becajava.ms_transaction_api.core.domain.Transacao;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface TransacaoGateway {
    Transacao salvar(Transacao transacao);
    Optional<Transacao> buscaPorId(UUID id);
}
