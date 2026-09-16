package br.com.medical.authservice.application.user.dto;

public class UpdateUserInput {

    private String userName;
    private String email;

    private UpdateUserInput(Builder builder) {
        this.userName = builder.userName;
        this.email = builder.email;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private String userName;
        private String email;

        public Builder username(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public UpdateUserInput build() {
            return new UpdateUserInput(this);
        }
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}
