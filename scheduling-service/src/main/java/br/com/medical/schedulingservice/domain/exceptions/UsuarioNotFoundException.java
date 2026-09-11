package br.com.medical.schedulingservice.domain.exceptions;

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(Long id) {
        super("Usuario nao encontrado para o id: " + id);
    }

    public UsuarioNotFoundException(String email) {
        super("Usuario nao encontrado para o email: " + email);
    }
}