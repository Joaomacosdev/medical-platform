package br.com.medical.authservice.presentation.user.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        name = "UpdatePasswordRequest",
        description = """
                Request payload used to update the password of an existing user.
                
                The password must contain between 8 and 100 characters and
                must include at least one uppercase letter, one lowercase letter,
                and one numeric character.
                
                For security reasons, the password must never be exposed in
                API responses or application logs.
                """
)
public class UpdatePasswordRequest {

    @NotBlank(message = "Password is required")
    @Size(
            min = 8,
            max = 100,
            message = "Password must be between 8 and 100 characters"
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain uppercase, lowercase and number"
    )
    @Schema(
            description = """
                    New password used for user authentication.
                    
                    The password must:
                    - Contain between 8 and 100 characters.
                    - Contain at least one uppercase letter.
                    - Contain at least one lowercase letter.
                    - Contain at least one number.
                    """,
            example = "Str0ngP@ssw0rd",
            minLength = 8,
            maxLength = 100,
            format = "password",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;


    public UpdatePasswordRequest() {
    }


    public UpdatePasswordRequest(String password) {
        this.password = password;
    }


    public String getPassword() {
        return password;
    }
}

