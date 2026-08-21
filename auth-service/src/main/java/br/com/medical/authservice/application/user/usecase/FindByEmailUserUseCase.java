package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.application.user.mapper.UserApplicationMapper;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.InvalidEmailException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;

public class FindByEmailUserUseCase {

    private final UserGateway userGateway;

    public FindByEmailUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserOutput execute(String email){

        User user = userGateway.findByEmail(email)
                .orElseThrow(() -> new InvalidEmailException(email));

        return UserApplicationMapper.toDto(user);

    }
}
