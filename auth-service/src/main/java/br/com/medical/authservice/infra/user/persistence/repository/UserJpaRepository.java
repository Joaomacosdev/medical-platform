package br.com.medical.authservice.infra.user.persistence.repository;

import br.com.medical.authservice.infra.user.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findById(Long id);

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByUserName(String username);

    List<UserJpaEntity> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByEmail(String email);
}
