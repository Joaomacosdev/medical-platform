package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.application.user.mapper.UserApplicationMapper;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.InvalidUserNameException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;

public class FindByUserNameUseCase {

    private final UserGateway userGateway;

    public FindByUserNameUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserOutput execute(String username){

        User user = userGateway.findByUserName(username)
                .orElseThrow(() -> new InvalidUserNameException(username));

        return UserApplicationMapper.toOutput(user);

    }
}
