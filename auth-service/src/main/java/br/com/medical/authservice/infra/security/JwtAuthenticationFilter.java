package br.com.medical.authservice.infra.security;

import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserGateway userGateway;


    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserGateway userGateway) {
        this.jwtTokenService = jwtTokenService;
        this.userGateway = userGateway;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = retrieveRequestToken(request);


        if (token != null) {
            String email = jwtTokenService.verify(token);

            var userOptional = userGateway.findByEmail(email);

            if (userOptional.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            User user = userOptional.get();

            var userDetails = new CustomUserDetails(user);


            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String retrieveRequestToken(HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return authorizationHeader.substring(7);
    }
}
