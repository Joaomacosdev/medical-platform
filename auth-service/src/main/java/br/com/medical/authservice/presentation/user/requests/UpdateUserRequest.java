package br.com.medical.authservice.presentation.user.requests;

public class UpdateUserRequest {
    private String userName;
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
