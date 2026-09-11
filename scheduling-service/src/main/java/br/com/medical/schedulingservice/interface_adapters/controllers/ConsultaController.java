package br.com.medical.schedulingservice.interface_adapters.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.medical.schedulingservice.application.dtos.ConsultaCreateRequest;
import br.com.medical.schedulingservice.application.dtos.ConsultaResponse;
import br.com.medical.schedulingservice.application.dtos.ConsultaUpdateRequest;
import br.com.medical.schedulingservice.domain.auth.UsuarioAutenticado;
import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.repositories.ConsultaFiltro;
import br.com.medical.schedulingservice.domain.usecases.CancelarConsultaComando;
import br.com.medical.schedulingservice.domain.usecases.CancelarConsultaUseCase;
import br.com.medical.schedulingservice.domain.usecases.CriarConsultaUseCase;
import br.com.medical.schedulingservice.domain.usecases.EditarConsultaComando;
import br.com.medical.schedulingservice.domain.usecases.EditarConsultaUseCase;
import br.com.medical.schedulingservice.domain.usecases.ListarConsultasComando;
import br.com.medical.schedulingservice.domain.usecases.ListarConsultasUseCase;
import br.com.medical.schedulingservice.domain.usecases.NovaConsultaComando;
import br.com.medical.schedulingservice.interface_adapters.mappers.ConsultaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Consultas")
@RestController
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final CriarConsultaUseCase criarConsultaUseCase;
    private final EditarConsultaUseCase editarConsultaUseCase;
    private final ListarConsultasUseCase listarConsultasUseCase;
    private final CancelarConsultaUseCase cancelarConsultaUseCase;
    private final ConsultaMapper consultaMapper;

    @Operation(summary = "Cria uma nova consulta")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    @PostMapping
    public ResponseEntity<ConsultaResponse> criar(@Valid @RequestBody ConsultaCreateRequest request,
                                                    @AuthenticationPrincipal UsuarioAutenticado solicitante) {
        NovaConsultaComando comando = new NovaConsultaComando(
                request.pacienteId(),
                request.profissionalId(),
                request.dataConsulta(),
                request.tipo(),
                request.observacoes(),
                solicitante.id(),
                solicitante.role()
        );
        Consulta consulta = criarConsultaUseCase.criar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaMapper.toResponse(consulta));
    }

    @Operation(summary = "Edita uma consulta existente")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> editar(@PathVariable Long id,
                                                     @Valid @RequestBody ConsultaUpdateRequest request,
                                                     @AuthenticationPrincipal UsuarioAutenticado solicitante) {
        EditarConsultaComando comando = new EditarConsultaComando(
                id,
                request.dataConsulta(),
                request.tipo(),
                request.status(),
                request.observacoes(),
                solicitante.id(),
                solicitante.role()
        );
        Consulta consulta = editarConsultaUseCase.editar(comando);
        return ResponseEntity.ok(consultaMapper.toResponse(consulta));
    }

    @Operation(summary = "Lista consultas com filtros opcionais por paciente, profissional, status e data")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    @GetMapping
    public ResponseEntity<List<ConsultaResponse>> listar(@RequestParam(required = false) Long pacienteId,
                                                            @RequestParam(required = false) Long profissionalId,
                                                            @RequestParam(required = false) ConsultaStatus status,
                                                            @RequestParam(required = false) LocalDate data,
                                                            @AuthenticationPrincipal UsuarioAutenticado solicitante) {
        ConsultaFiltro filtro = new ConsultaFiltro(pacienteId, profissionalId, status, data);
        ListarConsultasComando comando = new ListarConsultasComando(filtro, solicitante.id(), solicitante.role());
        List<Consulta> consultas = listarConsultasUseCase.listar(comando);
        return ResponseEntity.ok(consultaMapper.toResponseList(consultas));
    }

    @Operation(summary = "Busca uma consulta pelo id")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> buscarPorId(@PathVariable Long id,
                                                           @AuthenticationPrincipal UsuarioAutenticado solicitante) {
        Consulta consulta = listarConsultasUseCase.buscarPorId(id, solicitante.id(), solicitante.role());
        return ResponseEntity.ok(consultaMapper.toResponse(consulta));
    }

    @Operation(summary = "Cancela uma consulta")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ConsultaResponse> cancelar(@PathVariable Long id,
                                                        @AuthenticationPrincipal UsuarioAutenticado solicitante) {
        CancelarConsultaComando comando = new CancelarConsultaComando(id, solicitante.id(), solicitante.role());
        Consulta consulta = cancelarConsultaUseCase.cancelar(comando);
        return ResponseEntity.ok(consultaMapper.toResponse(consulta));
    }
}