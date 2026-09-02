package br.com.medical.authservice.domain.user.enums;

public enum Role {
    ADMIN, //
    MEDICO, //  podem visualizar e editar o histórico de consultas.
    ENFERMEIRO, // podem registrar consultas e acessar o histórico.
    PACIENTE //  podem visualizar apenas as suas consultas.

}
