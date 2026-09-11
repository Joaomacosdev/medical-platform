package br.com.medical.schedulingservice.domain.auth;

public record TokenEmitido(String token, long expiraEmSegundos) {
}