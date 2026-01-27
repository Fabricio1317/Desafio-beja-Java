package com.becajava.ms_transaction_worker.core.gateway;

public interface ValidadorGateway {
    double obterCotacaoDolar();
    boolean usuarioExiste(Long usuarioId);
}