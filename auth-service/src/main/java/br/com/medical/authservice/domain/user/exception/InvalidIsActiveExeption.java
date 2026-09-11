package br.com.medical.authservice.domain.user.exception;

public class InvalidIsActiveExeption extends RuntimeException {

    public InvalidIsActiveExeption() {
        super("Usuário inativo, não pode autenticar.");
    }
}
