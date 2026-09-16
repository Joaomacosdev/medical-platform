package br.com.medical.authservice.infra.security;

import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserGateway  userGateway;
    private final PasswordEncoder passwordEncoder;


    public CustomUserDetailsService(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userGateway.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));



        return new CustomUserDetails(user);
    }
}
