package br.com.medical.authservice.presentation.user.requests;

import br.com.medical.authservice.domain.user.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        name = "CreateUserRequest",
        description = """
                Request payload used to create a new user in the authentication service.
                
                All fields are required. The email must be valid and unique,
                the username must contain between 3 and 50 characters, and the
                password must contain between 8 and 100 characters.
                """
)
public class CreateUserRequest {

    @NotBlank(message = "Username is required")
    @Size(
            min = 3,
            max = 50,
            message = "Username must be between 3 and 50 characters"
    )
    @Schema(
            description = "Unique username used to identify the user.",
            example = "john.doe",
            minLength = 3,
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String userName;


    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(
            max = 100,
            message = "Email must not exceed 100 characters"
    )
    @Schema(
            description = "User's email address. The email must be valid and unique.",
            example = "john.doe@email.com",
            maxLength = 100,
            format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;


    @NotBlank(message = "Password is required")
    @Size(
            min = 8,
            max = 100,
            message = "Password must be between 8 and 100 characters"
    )
    @Schema(
            description = """
                    User password used for authentication.
                    
                    The password must contain between 8 and 100 characters.
                    For security reasons, passwords must never be exposed in
                    API responses or application logs.
                    """,
            example = "Str0ngP@ssw0rd",
            minLength = 8,
            maxLength = 100,
            format = "password",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;


    @NotNull(message = "Role is required")
    @Schema(
            description = "Authorization role assigned to the user.",
            example = "USER",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Role role;


    public CreateUserRequest() {
    }


    public CreateUserRequest(
            String userName,
            String email,
            String password,
            Role role
    ) {
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
