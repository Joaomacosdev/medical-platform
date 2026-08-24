package br.com.medical.authservice.presentation.user.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}
