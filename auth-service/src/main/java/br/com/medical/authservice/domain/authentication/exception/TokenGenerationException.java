package br.com.medical.authservice.domain.authentication.exception;

public class TokenGenerationException extends RuntimeException {

    public TokenGenerationException() {
        super("Não foi possível gerar o token de autenticação.");
    }
}
