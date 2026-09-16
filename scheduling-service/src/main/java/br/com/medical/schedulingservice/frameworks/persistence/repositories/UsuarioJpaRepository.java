package br.com.medical.schedulingservice.frameworks.persistence.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.medical.schedulingservice.frameworks.persistence.entities.UsuarioJpaEntity;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    Optional<UsuarioJpaEntity> findByAuthUserId(Long authUserId);

    Optional<UsuarioJpaEntity> findByEmail(String email);
}