package br.com.medical.authservice.presentation.user.mapper;

import br.com.medical.authservice.application.user.dto.CreateUserInput;
import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.presentation.user.requests.CreateUserRequest;
import br.com.medical.authservice.presentation.user.responses.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserPresentationMapper {

    public static CreateUserInput toInput(CreateUserRequest createUserRequest) {
        return CreateUserInput.builder()
                .userName(createUserRequest.getUserName())
                .email(createUserRequest.getEmail())
                .password(createUserRequest.getPassword())
                .role(createUserRequest.getRole())
                .build();
    }

    public static UserResponse toResponse(UserOutput output) {
        return new UserResponse(
                output.getId(),
                output.getUserName(),
                output.getEmail(),
                output.getRole()
        );
    }
}
