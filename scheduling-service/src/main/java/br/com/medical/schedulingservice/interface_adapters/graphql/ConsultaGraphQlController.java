package br.com.medical.schedulingservice.interface_adapters.graphql;

import java.time.LocalDate;
import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import br.com.medical.schedulingservice.application.dtos.ConsultaResponse;
import br.com.medical.schedulingservice.domain.auth.UsuarioAutenticado;
import br.com.medical.schedulingservice.domain.repositories.ConsultaFiltro;
import br.com.medical.schedulingservice.domain.usecases.ListarConsultasComando;
import br.com.medical.schedulingservice.domain.usecases.ListarConsultasUseCase;
import br.com.medical.schedulingservice.interface_adapters.mappers.ConsultaMapper;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConsultaGraphQlController {

    private final ListarConsultasUseCase listarConsultasUseCase;
    private final ConsultaMapper consultaMapper;

    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    @QueryMapping
    public List<ConsultaResponse> historicoConsultas(@Argument ConsultaFiltroInput filtro,
                                                        @AuthenticationPrincipal UsuarioAutenticado solicitante) {
        LocalDate data = filtro != null && filtro.data() != null ? LocalDate.parse(filtro.data()) : null;
        ConsultaFiltro dominioFiltro = new ConsultaFiltro(
                filtro != null ? filtro.pacienteId() : null,
                filtro != null ? filtro.profissionalId() : null,
                filtro != null ? filtro.status() : null,
                data
        );
        ListarConsultasComando comando = new ListarConsultasComando(dominioFiltro, solicitante.id(), solicitante.role());
        return consultaMapper.toResponseList(listarConsultasUseCase.listar(comando));
    }

    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    @QueryMapping
    public ConsultaResponse consulta(@Argument Long id, @AuthenticationPrincipal UsuarioAutenticado solicitante) {
        return consultaMapper.toResponse(listarConsultasUseCase.buscarPorId(id, solicitante.id(), solicitante.role()));
    }
}