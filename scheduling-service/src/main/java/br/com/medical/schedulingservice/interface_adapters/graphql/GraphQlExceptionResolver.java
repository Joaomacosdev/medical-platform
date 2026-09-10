package br.com.medical.schedulingservice.interface_adapters.graphql;

import java.util.List;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaNotFoundException;
import br.com.medical.schedulingservice.domain.exceptions.SlotIndisponivelException;
import br.com.medical.schedulingservice.domain.exceptions.UsuarioNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

@Component
public class GraphQlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        ErrorType errorType = switch (ex) {
            case ConsultaNotFoundException ignored -> ErrorType.NOT_FOUND;
            case UsuarioNotFoundException ignored -> ErrorType.NOT_FOUND;
            case ConsultaInvalidaException ignored -> ErrorType.BAD_REQUEST;
            case SlotIndisponivelException ignored -> ErrorType.BAD_REQUEST;
            case AcessoNegadoException ignored -> ErrorType.FORBIDDEN;
            case AccessDeniedException ignored -> ErrorType.FORBIDDEN;
            default -> null;
        };

        if (errorType == null) {
            return null;
        }

        return GraphqlErrorBuilder.newError(env)
                .errorType(errorType)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .build();
    }

    @Override
    protected java.util.List<GraphQLError> resolveToMultipleErrors(Throwable ex, DataFetchingEnvironment env) {
        GraphQLError single = resolveToSingleError(ex, env);
        return single == null ? null : List.of(single);
    }
}