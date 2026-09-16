package br.com.medical.authservice.domain.user.exception;

public class InvalidEmailException extends RuntimeException {

    private final String email;

    public InvalidEmailException(String email) {
        super("Invalid Email Address: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
