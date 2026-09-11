package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;

public class DeleteByIdUserUseCase {
    private final UserGateway userGateway;

    public DeleteByIdUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public void execute(Long id) {
        if (!userGateway.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userGateway.deleteById(id);
    }
}
