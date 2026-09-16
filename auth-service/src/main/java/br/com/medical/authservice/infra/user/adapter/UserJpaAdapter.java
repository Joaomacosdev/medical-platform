package br.com.medical.authservice.infra.user.adapter;

import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import br.com.medical.authservice.infra.user.mapper.UserPersistenceMapper;
import br.com.medical.authservice.infra.user.persistence.entity.UserJpaEntity;
import br.com.medical.authservice.infra.user.persistence.repository.UserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserJpaAdapter implements UserGateway {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserJpaAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper mapper, PasswordEncoder passwordEncoder) {

        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpa(user);

        entity.setPassword(passwordEncoder.encode(user.getPassword()));


        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUserName(String username) {
        return userJpaRepository.findByUserName(username).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {

        return userJpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {

        return userJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
