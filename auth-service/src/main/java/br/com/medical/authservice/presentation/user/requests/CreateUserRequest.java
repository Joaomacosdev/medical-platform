package br.com.medical.authservice.presentation.user.requests;

import br.com.medical.authservice.domain.user.enums.Role;

public class CreateUserRequest {
    private String userName;
    private String email;
    private String password;
    private Role role;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String userName, String email, String password, Role role) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
