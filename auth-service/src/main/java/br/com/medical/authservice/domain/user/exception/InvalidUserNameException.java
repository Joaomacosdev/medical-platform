package br.com.medical.authservice.domain.user.exception;

public class InvalidUserNameException extends RuntimeException {

    private final String userName;

    public InvalidUserNameException(String userName) {
        super("Invalid user name: " + userName);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
