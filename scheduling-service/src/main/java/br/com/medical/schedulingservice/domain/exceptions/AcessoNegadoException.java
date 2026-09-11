package br.com.medical.schedulingservice.domain.exceptions;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String message) {
        super(message);
    }
}