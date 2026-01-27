package com.becajava.ms_transaction_api.core.gateway;

import com.becajava.ms_transaction_api.core.domain.Transacao;


public interface MensageriaGateway {
    void enviar(Transacao transacao);
}