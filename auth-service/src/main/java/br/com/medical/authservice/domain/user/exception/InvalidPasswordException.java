package br.com.medical.authservice.domain.user.exception;

public class InvalidPasswordException extends RuntimeException {

    private final String password;

    public InvalidPasswordException(String password) {
        super("Invalid Password: " + password);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
