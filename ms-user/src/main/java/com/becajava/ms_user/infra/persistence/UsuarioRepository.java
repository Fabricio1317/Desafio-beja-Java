package com.becajava.ms_user.infra.persistence;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
