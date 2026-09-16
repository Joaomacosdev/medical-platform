package br.com.medical.schedulingservice.application.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.medical.schedulingservice.domain.entities.AvailableSlot;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;
import br.com.medical.schedulingservice.domain.exceptions.UsuarioNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.AvailableSlotRepository;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import br.com.medical.schedulingservice.domain.usecases.ConsultarDisponibilidadeUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultarDisponibilidadeService implements ConsultarDisponibilidadeUseCase {

    private final AvailableSlotRepository availableSlotRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AvailableSlot> consultar(Long profissionalId, LocalDate data) {
        Usuario profissional = usuarioRepository.buscarPorId(profissionalId)
                .orElseThrow(() -> new UsuarioNotFoundException(profissionalId));
        if (!profissional.isProfissionalDeSaude()) {
            throw new ConsultaInvalidaException("O usuario informado nao e medico nem enfermeiro.");
        }
        return availableSlotRepository.buscarDisponiveisPorProfissionalEData(profissionalId, data);
    }
}