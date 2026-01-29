package com.becajava.ms_transaction_api.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, UUID> {

    List<TransacaoEntity> findByUsuarioIdAndDataCriacaoBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fim);

    List<TransacaoEntity> findAllByUsuarioId(Long usuarioId);
}