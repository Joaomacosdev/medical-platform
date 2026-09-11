package br.com.medical.authservice.infra.user.mapper;

import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.infra.user.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {
    public UserJpaEntity toJpa(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.isActive()
        );
    }

    public User toDomain(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .userName(entity.getUserName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(entity.getRole())
                .isActive(entity.isActive())
                .build();
    }
}
