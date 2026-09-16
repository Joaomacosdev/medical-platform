package br.com.medical.schedulingservice.frameworks.security;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.exceptions.JWTVerificationException;
import br.com.medical.schedulingservice.domain.auth.UsuarioAutenticado;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            br.com.medical.schedulingservice.domain.auth.IdentidadeAutenticada identity;
            try {
                identity = jwtService.verificar(header.substring(7));
            } catch (JWTVerificationException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
                error(response, 401, "TOKEN_INVALIDO");
                return;
            }
            Object principal = identity;
            String path = request.getServletPath();
            boolean cadastro = path.equals("/api/v1/cadastro/me");
            boolean business = path.startsWith("/api/") || path.equals("/graphql");
            if (business && !cadastro) {
                if (identity.role().equals("ADMIN")) {
                    error(response, 403, "PERFIL_NAO_SUPORTADO");
                    return;
                }
                var usuario = usuarioRepository.buscarPorAuthUserId(identity.authUserId());
                if (usuario.isEmpty()) {
                    error(response, 409, "CADASTRO_PENDENTE");
                    return;
                }
                var local = usuario.get();
                principal = new UsuarioAutenticado(local.getId(), identity.email(),
                        local.getNome(), UserRole.valueOf(identity.role()));
            }
            var authentication = new UsernamePasswordAuthenticationToken(principal, null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + identity.role())));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private void error(HttpServletResponse response, int status, String code) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"code\":\"" + code + "\"}");
    }
}
