package br.com.medical.schedulingservice.domain.exceptions;

public class ConsultaNotFoundException extends RuntimeException {

    public ConsultaNotFoundException(Long id) {
        super("Consulta nao encontrada para o id: " + id);
    }
}