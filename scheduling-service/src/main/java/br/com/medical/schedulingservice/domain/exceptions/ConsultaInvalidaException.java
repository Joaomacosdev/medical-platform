package br.com.medical.schedulingservice.domain.exceptions;

public class ConsultaInvalidaException extends RuntimeException {

    public ConsultaInvalidaException(String message) {
        super(message);
    }
}