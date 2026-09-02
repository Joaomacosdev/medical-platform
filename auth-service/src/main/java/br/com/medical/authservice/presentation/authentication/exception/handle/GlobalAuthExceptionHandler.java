package br.com.medical.authservice.presentation.authentication.exception.handle;

import br.com.medical.authservice.domain.authentication.exception.InvalidCredentialsException;
import br.com.medical.authservice.domain.authentication.exception.InvalidTokenException;
import br.com.medical.authservice.domain.authentication.exception.TokenGenerationException;
import br.com.medical.authservice.presentation.authentication.exception.ProblemDetailFactory;
import br.com.medical.authservice.presentation.authentication.exception.docs.GlobalExceptionHandlerDocs;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalAuthExceptionHandler implements GlobalExceptionHandlerDocs {

    @ExceptionHandler(InvalidCredentialsException.class)
    @Override
    public ProblemDetail handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Invalid authentication credentials.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    @Override
    public ProblemDetail handleInvalidToken(
            InvalidTokenException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.UNAUTHORIZED,
                "INVALID_TOKEN",
                "The authentication token is invalid or expired.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(TokenGenerationException.class)
    @Override
    public ProblemDetail handleTokenGeneration(
            TokenGenerationException ex,
            HttpServletRequest request) {

        return ProblemDetailFactory.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "TOKEN_GENERATION_ERROR",
                "The authentication token could not be generated.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }
}
