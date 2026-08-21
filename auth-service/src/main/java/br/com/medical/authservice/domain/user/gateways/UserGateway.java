package br.com.medical.authservice.domain.user.gateways;

import br.com.medical.authservice.domain.user.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserGateway {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String username);
    List<User> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
}
