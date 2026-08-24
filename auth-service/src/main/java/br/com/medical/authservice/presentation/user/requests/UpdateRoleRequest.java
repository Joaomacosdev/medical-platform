package br.com.medical.authservice.presentation.user.requests;

import br.com.medical.authservice.domain.user.enums.Role;

public class UpdateRoleRequest {

    private Role role;

    public UpdateRoleRequest() {
    }

    public UpdateRoleRequest(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
