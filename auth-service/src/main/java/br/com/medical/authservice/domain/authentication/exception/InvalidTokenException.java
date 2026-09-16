package br.com.medical.authservice.domain.authentication.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Token de autenticação inválido ou expirado.");
    }
}