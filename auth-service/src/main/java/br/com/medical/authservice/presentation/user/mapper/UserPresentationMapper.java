package br.com.medical.authservice.presentation.user.mapper;

import br.com.medical.authservice.application.user.dto.*;
import br.com.medical.authservice.presentation.user.requests.CreateUserRequest;
import br.com.medical.authservice.presentation.user.requests.UpdatePasswordRequest;
import br.com.medical.authservice.presentation.user.requests.UpdateRoleRequest;
import br.com.medical.authservice.presentation.user.requests.UpdateUserRequest;
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

    public static UpdateUserInput toInput(UpdateUserRequest updateUserRequest) {
        return UpdateUserInput.builder()
                .username(updateUserRequest.getUserName())
                .email(updateUserRequest.getEmail())
                .build();
    }

    public static UpdateRoleInput toInput(UpdateRoleRequest updateRoleRequest) {
        return UpdateRoleInput.builder()
                .role(updateRoleRequest.getRole())
                .build();
    }

    public static UpdatePasswordInput toInput(UpdatePasswordRequest updatePasswordRequest){
        return UpdatePasswordInput.builder()
                .password(updatePasswordRequest.getPassword())
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
