package br.com.medical.schedulingservice.domain.auth;

/** Identity issued by auth; id is never a scheduling user ID. */
public record IdentidadeAutenticada(Long authUserId, String email, String role) {}
