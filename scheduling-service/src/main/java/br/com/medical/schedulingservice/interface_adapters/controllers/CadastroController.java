package br.com.medical.schedulingservice.interface_adapters.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import br.com.medical.schedulingservice.application.dtos.*;
import br.com.medical.schedulingservice.application.services.CadastroService;
import br.com.medical.schedulingservice.domain.auth.IdentidadeAutenticada;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cadastro/me")
@PreAuthorize("hasAnyRole('PACIENTE', 'MEDICO', 'ENFERMEIRO')")
@RequiredArgsConstructor
public class CadastroController {
    private final CadastroService service;

    @GetMapping
    public CadastroResponse buscar(@AuthenticationPrincipal IdentidadeAutenticada identity) {
        return CadastroResponse.from(service.buscar(identity.authUserId()));
    }

    /** Creates or replaces the caller's profile; repeated calls preserve its local ID. */
    @PutMapping
    public CadastroResponse salvar(@AuthenticationPrincipal IdentidadeAutenticada identity,
                                   @Valid @RequestBody CadastroRequest request) {
        return CadastroResponse.from(service.salvar(identity, request));
    }
}
