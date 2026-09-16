package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.application.user.mapper.UserApplicationMapper;
import br.com.medical.authservice.domain.user.gateways.UserGateway;

import java.util.List;

public class ListUserUseCase {

    private final UserGateway userGateway;


    public ListUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public List<UserOutput> execute(){
       return userGateway.findAll().stream().map(UserApplicationMapper::toOutput).toList();
    }
}
