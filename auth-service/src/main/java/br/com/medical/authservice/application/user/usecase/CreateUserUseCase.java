package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.CreateUserInput;
import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.application.user.mapper.UserApplicationMapper;
import br.com.medical.authservice.domain.user.exception.EmailAlreadyExistsException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

public class CreateUserUseCase {

    private final UserGateway userGateway;


    public CreateUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserOutput execute(CreateUserInput input)  {

        if(userGateway.existsByEmail(input.getEmail())){
            throw new EmailAlreadyExistsException(input.getEmail());
        }

        var user = UserApplicationMapper.toDomain(input);
        var savedUser = userGateway.save(user);

        return UserApplicationMapper.toOutput(savedUser);

    }
}
