package br.com.medical.authservice.domain.user.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("A role do usuário é obrigatória");
    }
}