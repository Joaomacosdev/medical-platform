package br.com.medical.historyservice.infra.config;

import br.com.medical.historyservice.application.history.usecase.GetAppointmentHistoryUseCase;
import br.com.medical.historyservice.domain.history.gateway.AppointmentHistoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class HistoryUseCaseConfig {

    @Bean
    public GetAppointmentHistoryUseCase getAppointmentHistoryUseCase(
            AppointmentHistoryGateway appointmentHistoryGateway) {
        return new GetAppointmentHistoryUseCase(appointmentHistoryGateway, Clock.systemUTC());
    }
}
