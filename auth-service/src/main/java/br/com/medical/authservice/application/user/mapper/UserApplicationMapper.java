package br.com.medical.authservice.application.user.mapper;

import br.com.medical.authservice.application.user.dto.CreateUserInput;
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

    public static UserOutput toDto(User user){
        return UserOutput.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

}
