package br.com.medical.authservice.presentation.user.exception.handle;

import br.com.medical.authservice.domain.user.exception.*;
import br.com.medical.authservice.presentation.user.exception.ProblemDetailFactory;
import br.com.medical.authservice.presentation.user.exception.docs.GlobalExceptionHandlerDocs;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler implements GlobalExceptionHandlerDocs {

    @ExceptionHandler(UserNotFoundException.class)
    @Override
    public ProblemDetail handleNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return   ProblemDetailFactory.create(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "The requested user was not found.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @Override
    public ProblemDetail handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailFactory.create(
                HttpStatus.CONFLICT,
                "EMAIL_ALREADY_EXISTS",
                "The provided email is already registered.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidEmailException.class)
    @Override
    public ProblemDetail handleInvalidEmail(
            InvalidEmailException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "INVALID_EMAIL",
                "The provided email address is invalid.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidIsActiveExeption.class)
    @Override
    public ProblemDetail handleInactiveUser(
            InvalidIsActiveExeption ex,
            HttpServletRequest request
    ) {
        return ProblemDetailFactory.create(
                HttpStatus.FORBIDDEN,
                "USER_INACTIVE",
                "The user is inactive and cannot authenticate.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @Override
    public ProblemDetail handleInvalidPassword(
            InvalidPasswordException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "INVALID_PASSWORD",
                "The provided password is invalid.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidRoleException.class)
    @Override
    public ProblemDetail handleInvalidRole(
            InvalidRoleException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "INVALID_ROLE",
                "The user role is required.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidUserNameException.class)
    @Override
    public ProblemDetail handleInvalidUserName(
            InvalidUserNameException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailFactory.create(
                HttpStatus.BAD_REQUEST,
                "INVALID_USERNAME",
                "The provided username is invalid.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

}
