package br.com.medical.historyservice.presentation.history.graphql.controller;

import br.com.medical.historyservice.application.history.usecase.GetAppointmentHistoryUseCase;
import br.com.medical.historyservice.presentation.history.graphql.mapper.HistoryGraphQlMapper;
import br.com.medical.historyservice.presentation.history.graphql.response.AppointmentHistoryResponse;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class HistoryGraphQlController {

    private final GetAppointmentHistoryUseCase getAppointmentHistoryUseCase;

    public HistoryGraphQlController(GetAppointmentHistoryUseCase getAppointmentHistoryUseCase) {
        this.getAppointmentHistoryUseCase = getAppointmentHistoryUseCase;
    }

    @QueryMapping
    public List<AppointmentHistoryResponse> appointmentHistory(
            @Argument String patientId,
            @Argument boolean futureOnly) {
        return getAppointmentHistoryUseCase.execute(patientId, futureOnly)
                .stream()
                .map(HistoryGraphQlMapper::toResponse)
                .toList();
    }

    @GraphQlExceptionHandler(IllegalArgumentException.class)
    public GraphQLError handleInvalidArgument(
            GraphqlErrorBuilder<?> errorBuilder,
            IllegalArgumentException exception) {
        return errorBuilder
                .errorType(ErrorType.BAD_REQUEST)
                .message(exception.getMessage())
                .build();
    }
}
