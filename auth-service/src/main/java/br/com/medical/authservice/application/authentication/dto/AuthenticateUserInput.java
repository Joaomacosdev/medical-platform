package br.com.medical.authservice.application.authentication.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticateUserInput {

    private final String email;
    private final String password;

    @JsonCreator
    public AuthenticateUserInput(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password
    ) {
        this.email = email;
        this.password = password;
    }

    private AuthenticateUserInput(Builder builder) {
        this(builder.email, builder.password);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String email;
        private String password;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public AuthenticateUserInput build() {
            return new AuthenticateUserInput(this);
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}