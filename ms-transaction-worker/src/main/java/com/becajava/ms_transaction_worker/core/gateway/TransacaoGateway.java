package com.becajava.ms_transaction_worker.core.gateway;

import com.becajava.ms_transaction_worker.core.domain.Transacao;
import java.util.UUID; // Não esqueça desse import

public interface TransacaoGateway {
    void atualizar(Transacao transacao);


    void atualizarStatus(UUID id, String status);
}