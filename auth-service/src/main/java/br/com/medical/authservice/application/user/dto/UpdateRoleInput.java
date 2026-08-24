package br.com.medical.authservice.application.user.dto;

import br.com.medical.authservice.domain.user.enums.Role;

public class UpdateRoleInput {

    private Role role;

    private UpdateRoleInput(Builder builder){
        role = builder.role;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private Role role;

        public Builder role(Role role   ){
            this.role = role;
            return this;
        }

        public UpdateRoleInput build(){
            return new UpdateRoleInput(this);
        }
    }

    public Role getRole() {
        return role;
    }
}
