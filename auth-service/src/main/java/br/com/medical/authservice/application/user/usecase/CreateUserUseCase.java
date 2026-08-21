package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.domain.user.gateways.UserGateway;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;

public class CreateUserUseCase {

    private final UserGateway userGateway;

    public CreateUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public User execute(User userRequest) {

        User user = userGateway.findById(userRequest.getId())
                .orElseThrow(() -> new UserNotFoundException(userRequest.getId()));

        if (!userGateway.existsById(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }

        if (!userGateway.existsByEmail(user.getEmail())) {
            throw new UserNotFoundException(user.getId());
        }

        userGateway.save(user);

        return user;

    }
}
