package br.com.medical.authservice.presentation.user.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdatePasswordRequest {

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain uppercase, lowercase and number"
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
