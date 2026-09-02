package br.com.medical.authservice.presentation.authentication.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "AuthenticateUserRequest",
        description = """
                Request payload used to authenticate a user.
                
                The user must provide a valid email address and password.
                The provided credentials are validated by the authentication service.
                """
)
public class AuthenticateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(
            max = 100,
            message = "Email must not exceed 100 characters"
    )
    @Schema(
            description = "Email address associated with the user account.",
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
                    Password used to authenticate the user.
                    
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


    public AuthenticateUserRequest() {
    }


    public AuthenticateUserRequest(
            String email,
            String password
    ) {
        this.email = email;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

