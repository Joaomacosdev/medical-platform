package br.com.medical.authservice.domain.user.entities;

import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.domain.user.exception.*;

import java.util.Objects;
import java.util.regex.Pattern;

public class User {

    private Long id;
    private String userName;
    private String email;
    private String password;
    private Role role;
    private boolean isActive;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+\\.[A-Za-z0-9.-]+$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    private User(Builder builder) {

        String normalizedEmail = normalizeEmail(builder.email);


        validateUserName(builder.userName);
        validateEmail(normalizedEmail);
        validatePassword(builder.password);
        validateRole(builder.role);

        this.id = builder.id;
        this.userName = builder.userName;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
        this.isActive = builder.isActive;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String userName;
        private String email;
        private String password;
        private Role role;
        private boolean isActive = true;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateProfile(String newUserName, String newEmail) {
        String normalizedEmail = normalizeEmail(newEmail);

        validateUserName(newUserName);
        validateEmail(normalizedEmail);

        this.userName = newUserName;
        this.email = normalizedEmail;
    }

    public void changePassword(String newPassword) {
        validatePassword(newPassword);
        this.password = newPassword;
    }

    public void changeRole(Role newRole) {
        validateRole(newRole);
        this.role = newRole;
    }

    private void validateUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new InvalidUserNameException(userName);
        }
    }

    private void validateEmail(String email) {
        if (email == null ||
                email.isBlank() ||
                !EMAIL_PATTERN.matcher(email).matches()) {

            throw new InvalidEmailException(email);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank() || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidPasswordException(password);
        }
    }

    private void validateRole(Role role) {
        if (role == null) {
            throw new InvalidRoleException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}