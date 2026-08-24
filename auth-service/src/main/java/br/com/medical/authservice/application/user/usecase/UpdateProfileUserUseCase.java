package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UpdateUserInput;
import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.application.user.mapper.UserApplicationMapper;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;

public class UpdateProfileUserUseCase {

    private final UserGateway userGateway;


    public UpdateProfileUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserOutput execute(Long id, UpdateUserInput input) {

        User user = userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));


        user.updateProfile(
                input.getUserName(),
                input.getEmail()
        );


        User updatedUser = userGateway.save(user);

        return UserApplicationMapper.toOutput(updatedUser);

    }

}
