package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteByIdUserUseCase Tests")
class DeleteByIdUserUseCaseTest {

    private static final Long USER_ID = 1L;

    @Mock
    private UserGateway userGateway;

    private DeleteByIdUserUseCase deleteByIdUserUseCase;

    @BeforeEach
    void setUp() {
        deleteByIdUserUseCase = new DeleteByIdUserUseCase(userGateway);
    }

    @Test
    @DisplayName("Should delete user when user exists")
    void shouldDeleteUserWhenUserExists() {

        when(userGateway.existsById(USER_ID))
                .thenReturn(true);

        deleteByIdUserUseCase.execute(USER_ID);

        verify(userGateway)
                .existsById(USER_ID);

        verify(userGateway)
                .deleteById(USER_ID);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userGateway.existsById(USER_ID))
                .thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> deleteByIdUserUseCase.execute(USER_ID)
        );

        verify(userGateway)
                .existsById(USER_ID);

        verify(userGateway, never())
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("Should check user existence before deleting")
    void shouldCheckUserExistenceBeforeDeleting() {

        when(userGateway.existsById(USER_ID))
                .thenReturn(true);

        deleteByIdUserUseCase.execute(USER_ID);

        InOrder inOrder = inOrder(userGateway);

        inOrder.verify(userGateway)
                .existsById(USER_ID);

        inOrder.verify(userGateway)
                .deleteById(USER_ID);
    }

    @Test
    @DisplayName("Should pass the correct id to gateway")
    void shouldPassCorrectIdToGateway() {

        when(userGateway.existsById(USER_ID))
                .thenReturn(true);

        deleteByIdUserUseCase.execute(USER_ID);

        verify(userGateway)
                .existsById(eq(USER_ID));

        verify(userGateway)
                .deleteById(eq(USER_ID));
    }

    @Test
    @DisplayName("Should not delete any user when user does not exist")
    void shouldNotDeleteAnyUserWhenUserDoesNotExist() {

        when(userGateway.existsById(USER_ID))
                .thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> deleteByIdUserUseCase.execute(USER_ID)
        );

        verify(userGateway, never())
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("Should propagate exception thrown when checking user existence")
    void shouldPropagateExceptionWhenCheckingUserExistence() {

        RuntimeException exception =
                new RuntimeException("Database unavailable");

        when(userGateway.existsById(USER_ID))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> deleteByIdUserUseCase.execute(USER_ID)
        );

        org.junit.jupiter.api.Assertions.assertSame(
                exception,
                thrown
        );

        verify(userGateway)
                .existsById(USER_ID);

        verify(userGateway, never())
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("Should propagate exception thrown when deleting user")
    void shouldPropagateExceptionWhenDeletingUser() {

        when(userGateway.existsById(USER_ID))
                .thenReturn(true);

        RuntimeException exception =
                new RuntimeException("Database error");

        doThrow(exception)
                .when(userGateway)
                .deleteById(USER_ID);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> deleteByIdUserUseCase.execute(USER_ID)
        );

        org.junit.jupiter.api.Assertions.assertSame(
                exception,
                thrown
        );

        verify(userGateway)
                .existsById(USER_ID);

        verify(userGateway)
                .deleteById(USER_ID);
    }
}

