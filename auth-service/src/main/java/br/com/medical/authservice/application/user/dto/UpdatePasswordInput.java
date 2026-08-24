package br.com.medical.authservice.application.user.dto;

public class UpdatePasswordInput {

    private String password;

    private UpdatePasswordInput(Builder builder){
        this.password = builder.password;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private String password;

        public Builder password(String password){
            this.password = password;
            return this;
        }

        public UpdatePasswordInput build(){
            return new UpdatePasswordInput(this);
        }
    }

    public String getPassword() {
        return password;
    }
}
