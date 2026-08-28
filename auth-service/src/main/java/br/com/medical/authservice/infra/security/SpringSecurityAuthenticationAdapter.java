package br.com.medical.authservice.infra.security;

import br.com.medical.authservice.domain.authentication.exception.InvalidCredentialsException;
import br.com.medical.authservice.domain.authentication.gatewys.AuthenticationGateway;
import br.com.medical.authservice.domain.authentication.gatewys.AuthenticationResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuthenticationAdapter implements AuthenticationGateway {
    private final AuthenticationManager authenticationManager;

    public SpringSecurityAuthenticationAdapter(
            AuthenticationManager authenticationManager
    ) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResult authenticate(
            String email,
            String password
    ) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    password
                            )
                    );

            CustomUserDetails userDetails =
                    (CustomUserDetails) authentication.getPrincipal();

            return new AuthenticationResult(
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getUser().getRole()
            );

        } catch (Exception exception) {

            throw new InvalidCredentialsException();
        }
    }
}
