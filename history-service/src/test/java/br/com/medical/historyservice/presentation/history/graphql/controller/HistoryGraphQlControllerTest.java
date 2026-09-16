package br.com.medical.historyservice.presentation.history.graphql.controller;

import br.com.medical.historyservice.application.history.usecase.GetAppointmentHistoryUseCase;
import br.com.medical.historyservice.domain.history.model.AppointmentHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@GraphQlTest(HistoryGraphQlController.class)
class HistoryGraphQlControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private GetAppointmentHistoryUseCase getAppointmentHistoryUseCase;

    @Test
    void shouldReturnAppointmentHistoryThroughGraphQl() {
        AppointmentHistory history = new AppointmentHistory(
                "appointment-1",
                "patient-1",
                "doctor-1",
                OffsetDateTime.parse("2026-09-01T10:00:00Z"),
                "SCHEDULED");
        when(getAppointmentHistoryUseCase.execute("patient-1", true))
                .thenReturn(List.of(history));

        graphQlTester.document("""
                        query AppointmentHistory($patientId: ID!, $futureOnly: Boolean!) {
                          appointmentHistory(patientId: $patientId, futureOnly: $futureOnly) {
                            appointmentId
                            patientId
                            doctorId
                            scheduledAt
                            status
                          }
                        }
                        """)
                .variable("patientId", "patient-1")
                .variable("futureOnly", true)
                .execute()
                .path("appointmentHistory[0].appointmentId")
                .entity(String.class)
                .isEqualTo("appointment-1")
                .path("appointmentHistory[0].scheduledAt")
                .entity(String.class)
                .isEqualTo("2026-09-01T10:00Z")
                .path("appointmentHistory[0].status")
                .entity(String.class)
                .isEqualTo("SCHEDULED");

        verify(getAppointmentHistoryUseCase).execute("patient-1", true);
    }

    @Test
    void shouldUseFalseAsDefaultForFutureOnly() {
        when(getAppointmentHistoryUseCase.execute("patient-1", false))
                .thenReturn(List.of());

        graphQlTester.document("""
                        query {
                          appointmentHistory(patientId: "patient-1") {
                            appointmentId
                          }
                        }
                        """)
                .execute()
                .path("appointmentHistory")
                .entityList(Object.class)
                .hasSize(0);

        verify(getAppointmentHistoryUseCase).execute("patient-1", false);
    }

    @Test
    void shouldReturnBadRequestForBlankPatientId() {
        when(getAppointmentHistoryUseCase.execute(" ", false))
                .thenThrow(new IllegalArgumentException("patientId nao pode ser vazio"));

        graphQlTester.document("""
                        query {
                          appointmentHistory(patientId: " ") {
                            appointmentId
                          }
                        }
                        """)
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors)
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.getMessage()).isEqualTo("patientId nao pode ser vazio");
                            assertThat(error.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                        }));

        verify(getAppointmentHistoryUseCase).execute(" ", false);
    }
}
