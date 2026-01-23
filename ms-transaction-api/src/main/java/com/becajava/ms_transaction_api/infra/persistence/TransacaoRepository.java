package com.becajava.ms_transaction_api.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransacaoRepository extends JpaRepository<TransacaoEntity, UUID> {
}
