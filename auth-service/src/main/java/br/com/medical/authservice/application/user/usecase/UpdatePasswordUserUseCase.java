package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UpdatePasswordInput;
import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.application.user.mapper.UserApplicationMapper;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UpdatePasswordUserUseCase {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    public UpdatePasswordUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    public UserOutput execute(Long id, UpdatePasswordInput updatePasswordInput){

        User user = userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        String encodedPassword =
                passwordEncoder.encode(updatePasswordInput.getPassword());

        user.changePassword(encodedPassword);

        var savedUser = userGateway.save(user);

        return UserApplicationMapper.toOutput(savedUser);
    }
}
