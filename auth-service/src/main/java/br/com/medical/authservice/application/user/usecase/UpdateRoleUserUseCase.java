package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UpdateRoleInput;
import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.application.user.mapper.UserApplicationMapper;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;

public class UpdateRoleUserUseCase {

    private final UserGateway userGateway;

    public UpdateRoleUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserOutput execute(Long id, UpdateRoleInput input){

        User user = userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.changeRole(
                input.getRole()
        );

        User updatedUser = userGateway.save(user);

        return UserApplicationMapper.toOutput(updatedUser);

    }
}
