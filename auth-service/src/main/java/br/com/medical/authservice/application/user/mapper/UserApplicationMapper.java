package br.com.medical.authservice.application.user.mapper;

import br.com.medical.authservice.application.user.dto.CreateUserInput;
import br.com.medical.authservice.application.user.dto.UpdatePasswordInput;
import br.com.medical.authservice.application.user.dto.UpdateUserInput;
import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.domain.user.entities.User;

public class UserApplicationMapper {

    public static User toDomain(CreateUserInput createUserInput){
        return User.builder()
                .userName(createUserInput.getUserName())
                .email(createUserInput.getEmail())
                .password(createUserInput.getPassword())
                .role(createUserInput.getRole())
                .build();
    }

    public static User toDomain(UpdateUserInput updateUserInput){
        return User.builder()
                .userName(updateUserInput.getUserName())
                .email(updateUserInput.getEmail())
                .build();
    }

    public static User toDomain(UpdatePasswordInput updatePasswordInput){
        return User.builder()
                .password(updatePasswordInput.getPassword())
                .build();
    }

    public static UserOutput toOutput(User user){
        return UserOutput.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

}
