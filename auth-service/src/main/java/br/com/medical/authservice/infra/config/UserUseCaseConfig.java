package br.com.medical.authservice.infra.config;

import br.com.medical.authservice.application.user.usecase.*;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway) {
        return new CreateUserUseCase(userGateway);
    }

    @Bean
    public DeleteByIdUserUseCase deleteByIdUserUseCase(UserGateway userGateway) {
        return new DeleteByIdUserUseCase(userGateway);
    }

    @Bean
    public FindByEmailUserUseCase findByEmailUserUseCase(UserGateway userGateway) {
        return new FindByEmailUserUseCase(userGateway);
    }

    @Bean
    public FindByIdUserUseCase findByIdUserUseCase(UserGateway userGateway) {
        return new FindByIdUserUseCase(userGateway);
    }

    @Bean
    public FindByUserNameUseCase findByUserNameUseCase(UserGateway userGateway) {
        return new FindByUserNameUseCase(userGateway);
    }

    @Bean
    public ListUserUseCase listUserUseCase(UserGateway userGateway) {
        return new ListUserUseCase(userGateway);
    }

    @Bean
    public UpdateProfileUserUseCase updateProfileUserUseCase(UserGateway userGateway) {
        return new UpdateProfileUserUseCase(userGateway);
    }

    @Bean
    public UpdatePasswordUserUseCase updatePasswordUserUseCase(UserGateway userGateway) {
        return new UpdatePasswordUserUseCase(userGateway);
    }

    @Bean
    public UpdateRoleUserUseCase updateRoleUserUseCase(UserGateway userGateway) {
        return new UpdateRoleUserUseCase(userGateway);
    }

}
