package br.com.medical.authservice.presentation.user.controllers;

import br.com.medical.authservice.application.user.usecase.*;
import br.com.medical.authservice.infra.security.CustomUserDetails;
import br.com.medical.authservice.presentation.user.controllers.docs.UserControllerDocs;
import br.com.medical.authservice.presentation.user.mapper.UserPresentationMapper;
import br.com.medical.authservice.presentation.user.requests.CreateUserRequest;
import br.com.medical.authservice.presentation.user.requests.UpdatePasswordRequest;
import br.com.medical.authservice.presentation.user.requests.UpdateRoleRequest;
import br.com.medical.authservice.presentation.user.requests.UpdateUserRequest;
import br.com.medical.authservice.presentation.user.responses.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/v1")

public class UserController implements UserControllerDocs {

    private final CreateUserUseCase createUserUseCase;
    private final FindByIdUserUseCase findByIdUserUseCase;
    private final FindByEmailUserUseCase findByEmailUserUseCase;
    private final FindByUserNameUseCase findByUserNameUseCase;
    private final ListUserUseCase listUserUseCase;
    private final UpdateProfileUserUseCase updateProfileUserUseCase;
    private final UpdatePasswordUserUseCase updatePasswordUserUseCase;
    private final UpdateRoleUserUseCase updateRoleUserUseCase;

    private final DeleteByIdUserUseCase deleteByIdUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase, FindByIdUserUseCase findByIdUserUseCase, FindByEmailUserUseCase findByEmailUserUseCase, FindByUserNameUseCase findByUserNameUseCase, ListUserUseCase listUserUseCase, UpdateProfileUserUseCase updateProfileUserUseCase, UpdatePasswordUserUseCase updatePasswordUserUseCase, UpdateRoleUserUseCase updateRoleUserUseCase, DeleteByIdUserUseCase deleteByIdUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.findByIdUserUseCase = findByIdUserUseCase;
        this.findByEmailUserUseCase = findByEmailUserUseCase;
        this.findByUserNameUseCase = findByUserNameUseCase;
        this.listUserUseCase = listUserUseCase;
        this.updateProfileUserUseCase = updateProfileUserUseCase;
        this.updatePasswordUserUseCase = updatePasswordUserUseCase;
        this.updateRoleUserUseCase = updateRoleUserUseCase;
        this.deleteByIdUserUseCase = deleteByIdUserUseCase;
    }

    @PostMapping
    @Override
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        var input = UserPresentationMapper.toInput(createUserRequest);
        var output = createUserUseCase.execute(input);
        var response = UserPresentationMapper.toResponse(output);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<UserResponse> getCurrentUser(   @AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(UserPresentationMapper.toResponse(findByIdUserUseCase.execute(userDetails.getUserId())));
    }

    @GetMapping
    @PreAuthorize("hasRole('MEDICO')")
    @Override
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String userName) {

        if (email != null) {
            return ResponseEntity.ok(
                    UserPresentationMapper.toResponse(findByEmailUserUseCase.execute(email))
            );
        }

        if (userName != null) {
            return ResponseEntity.ok(
                    UserPresentationMapper.toResponse(findByUserNameUseCase.execute(userName))
            );
        }

        var response = listUserUseCase.execute()
                .stream()
                .map(UserPresentationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.userId")
    @Override
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest){

        var input = UserPresentationMapper.toInput(updateUserRequest);
        var output = updateProfileUserUseCase.execute(id, input);
        var response = UserPresentationMapper.toResponse(output);

        return ResponseEntity.ok(response);

    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.userId")
    @Override
    public ResponseEntity<UserResponse> updateUserPassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest){

        var input = UserPresentationMapper.toInput(updatePasswordRequest);
        var output = updatePasswordUserUseCase.execute(id, input);
        var response = UserPresentationMapper.toResponse(output);

        return ResponseEntity.ok(response);

    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")

    @Override
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest updateRoleRequest){

        var input = UserPresentationMapper.toInput(updateRoleRequest);
        var output = updateRoleUserUseCase.execute(id, input);
        var response = UserPresentationMapper.toResponse(output);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasRole('ADMIN') or #id == authentication.principal.userId"
    )
    @Override
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        deleteByIdUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }




}
