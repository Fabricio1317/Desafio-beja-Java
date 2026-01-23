package com.becajava.ms_transaction_worker.core.gateway;

import com.becajava.ms_transaction_worker.core.domain.Transacao;

public interface TransacaoGateway {
    void atualizar(Transacao transacao);
}
