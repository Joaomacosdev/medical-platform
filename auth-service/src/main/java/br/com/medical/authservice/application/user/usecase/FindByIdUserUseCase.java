package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.application.user.mapper.UserApplicationMapper;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;

public class FindByIdUserUseCase {

    private final UserGateway userGateway;

    public FindByIdUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserOutput execute(Long id) {
        User user = userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserApplicationMapper.toDto(user);
    }
}
