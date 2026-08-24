package br.com.medical.authservice.presentation.user.requests;

public class UpdatePasswordRequest {

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
