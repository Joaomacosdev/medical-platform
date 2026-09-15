package br.com.medical.schedulingservice.application.services;

import br.com.medical.schedulingservice.application.dtos.CadastroRequest;
import br.com.medical.schedulingservice.domain.auth.IdentidadeAutenticada;
import br.com.medical.schedulingservice.domain.entities.*;
import br.com.medical.schedulingservice.domain.exceptions.*;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadastroServiceTest {
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final CadastroService service = new CadastroService(repository);
    private final IdentidadeAutenticada patient = new IdentidadeAutenticada(7L, "login@example.com", "PACIENTE");

    @Test void createsProfileFromVerifiedIdentityWithIndependentContact() {
        when(repository.buscarPorAuthUserId(7L)).thenReturn(Optional.empty());
        when(repository.salvar(any())).thenAnswer(call -> call.getArgument(0));
        var profile = service.salvar(patient, new CadastroRequest("Ana", "contact@example.com", "11999999999", null));
        assertEquals(7L, profile.getAuthUserId());
        assertEquals("contact@example.com", profile.getEmail());
        assertEquals(UserRole.PACIENTE, profile.getRole());
        verify(repository, never()).buscarPorEmail(anyString());
    }

    @Test void repeatedRegistrationPreservesLocalIdAndDoesNotCreateAnotherProfile() {
        when(repository.buscarPorAuthUserId(7L)).thenReturn(Optional.of(
                Usuario.builder().id(42L).authUserId(7L).role(UserRole.PACIENTE).build()));
        when(repository.salvar(any())).thenAnswer(call -> call.getArgument(0));
        var request = new CadastroRequest("Ana", "contact@example.com", null, null);
        assertEquals(42L, service.salvar(patient, request).getId());
        assertEquals(42L, service.salvar(patient, request).getId());
    }

    @Test void professionalsNeedSpecialtyForNotifications() {
        var doctor = new IdentidadeAutenticada(8L, "doctor@example.com", "MEDICO");
        assertThrows(ConsultaInvalidaException.class,
            () -> service.salvar(doctor, new CadastroRequest("Doctor", "doctor@example.com", null, null)));
        verifyNoInteractions(repository);
    }

    @Test void missingProfileHasExplicitError() {
        when(repository.buscarPorAuthUserId(7L)).thenReturn(Optional.empty());
        assertThrows(CadastroPendenteException.class, () -> service.buscar(7L));
    }
}
