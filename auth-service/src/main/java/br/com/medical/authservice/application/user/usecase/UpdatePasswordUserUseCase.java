package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UpdatePasswordInput;
import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.application.user.mapper.UserApplicationMapper;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;

public class UpdatePasswordUserUseCase {

    private final UserGateway userGateway;

    public UpdatePasswordUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserOutput execute(Long id, UpdatePasswordInput updatePasswordInput){

        User user = userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.changePassword(updatePasswordInput.getPassword());

        User updatedUser = userGateway.save(user);

        return UserApplicationMapper.toOutput(updatedUser);

    }
}
