package br.com.medical.schedulingservice.domain.exceptions;

public class SlotIndisponivelException extends RuntimeException {

    public SlotIndisponivelException(String message) {
        super(message);
    }
}