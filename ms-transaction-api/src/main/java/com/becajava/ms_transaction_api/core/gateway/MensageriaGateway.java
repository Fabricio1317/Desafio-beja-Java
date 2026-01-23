package com.becajava.ms_transaction_api.core.gateway;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import org.springframework.stereotype.Service;

@Service
public interface MensageriaGateway {
    void enviarParaFila(Transacao transacao);
}
