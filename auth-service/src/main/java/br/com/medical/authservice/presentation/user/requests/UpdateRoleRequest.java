package br.com.medical.authservice.presentation.user.requests;

import br.com.medical.authservice.domain.user.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "UpdateRoleRequest",
        description = """
                Request payload used to update the authorization role
                assigned to an existing user.
                
                The role must be one of the authorization roles supported
                by the authentication service.
                """
)
public class UpdateRoleRequest {

    @NotNull(message = "Role is required")
    @Schema(
            description = """
                    Authorization role to be assigned to the user.
                    
                    The available roles are defined by the Role enum.
                    Changing a user's role may affect the permissions
                    available to that user.
                    """,
            example = "USER",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
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
