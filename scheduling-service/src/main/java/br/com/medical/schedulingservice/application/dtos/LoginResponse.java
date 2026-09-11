package br.com.medical.schedulingservice.application.dtos;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEmSegundos,
        String nome,
        String email,
        String role
) {
}