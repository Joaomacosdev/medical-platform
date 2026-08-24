package br.com.medical.authservice.application.user.dto;

import br.com.medical.authservice.domain.user.enums.Role;

public class CreateUserInput {
    private Long id;
    private String userName;
    private String email;
    private String password;
    private Role role;

    private CreateUserInput(Builder builder) {
        this.id = builder.id;
        this.userName = builder.userName;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
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

        public CreateUserInput build() {
            return new CreateUserInput(this);
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
}
