package br.com.medical.authservice.presentation.user.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(
        name = "UpdateUserRequest",
        description = """
                Request payload used to update the profile information
                of an existing user.
                
                Both fields are optional. Only the fields provided in the
                request will be considered for update.
                
                The username, when provided, must contain between 3 and 50
                characters. The email, when provided, must be valid and
                contain no more than 100 characters.
                """
)
public class UpdateUserRequest {

    @Size(
            min = 3,
            max = 50,
            message = "Username must be between 3 and 50 characters"
    )
    @Schema(
            description = """
                    New username for the user.
                    
                    This field is optional. When provided, it must contain
                    between 3 and 50 characters.
                    """,
            example = "john.doe",
            minLength = 3,
            maxLength = 50
    )
    private String userName;


    @Email(message = "Email must be valid")
    @Size(
            max = 100,
            message = "Email must not exceed 100 characters"
    )
    @Schema(
            description = """
                    New email address for the user.
                    
                    This field is optional. When provided, it must be a valid
                    email address and contain no more than 100 characters.
                    """,
            example = "john.doe@email.com",
            maxLength = 100,
            format = "email"
    )
    private String email;


    public UpdateUserRequest() {
    }


    public UpdateUserRequest(
            String userName,
            String email
    ) {
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

