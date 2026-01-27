package com.becajava.ms_transaction_worker.core.gateway;

import com.becajava.ms_transaction_worker.core.domain.StatusTransacao;
import com.becajava.ms_transaction_worker.core.domain.Transacao;
import java.util.UUID;

public interface TransacaoGateway {
    void atualizar(Transacao transacao);
    void atualizarStatus(UUID id, StatusTransacao novoStatus);
}