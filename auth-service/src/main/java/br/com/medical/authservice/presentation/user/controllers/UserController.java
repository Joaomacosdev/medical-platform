package br.com.medical.authservice.presentation.user.controllers;

import br.com.medical.authservice.application.user.usecase.CreateUserUseCase;
import br.com.medical.authservice.presentation.user.mapper.UserPresentationMapper;
import br.com.medical.authservice.presentation.user.requests.CreateUserRequest;
import br.com.medical.authservice.presentation.user.responses.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        var input = UserPresentationMapper.toInput(createUserRequest);
        var output = createUserUseCase.execute(input);
        var response = UserPresentationMapper.toResponse(output);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
