package br.com.medical.authservice.domain.user.exception;

public class UserNotFoundException  extends RuntimeException {
    private final Long userId;

    public UserNotFoundException(Long userId) {
        super("User not found");
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}