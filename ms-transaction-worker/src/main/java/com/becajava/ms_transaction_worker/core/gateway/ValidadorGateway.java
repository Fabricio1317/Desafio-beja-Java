package com.becajava.ms_transaction_worker.core.gateway;


public interface ValidadorGateway {
    boolean usuarioExiste(Long id);
    Double obterCotacaoDolar();

}
