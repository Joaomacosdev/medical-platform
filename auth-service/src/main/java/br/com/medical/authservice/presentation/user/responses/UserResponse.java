package br.com.medical.authservice.presentation.user.responses;

import br.com.medical.authservice.domain.user.enums.Role;

public class UserResponse {
    private Long id;
    private String userName;
    private String email;
    private Role role;

    public UserResponse() {
    }

    public UserResponse(Long id, String userName, String email, Role role) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.role = role;
    }



    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }



    public Role getRole() {
        return role;
    }


}
