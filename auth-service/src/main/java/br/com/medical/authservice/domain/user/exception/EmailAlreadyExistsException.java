package br.com.medical.authservice.domain.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    private final String email;

    public EmailAlreadyExistsException(String email) {
        super("Email already exists");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
