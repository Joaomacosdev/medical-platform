package br.com.medical.authservice.application.authentication.dto;

public class RefreshTokenInput {

    private String refreshToken;


    private RefreshTokenInput(Builder builder) {
        this.refreshToken = builder.refreshToken;

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{
        private String refreshToken;



        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }



        public RefreshTokenInput build() {
            return new RefreshTokenInput(this);
        }
    }




    public String getRefreshToken() {
        return refreshToken;
    }




}
