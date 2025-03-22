package br.com.bix.images.config.security;

import br.com.bix.images.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
@RequiredArgsConstructor
public class WebSecurity {

    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain springWebFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider, CorsConfigurationSource corsConfigurationSource) throws Exception {

        return http
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(authorize -> authorize.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))
            .exceptionHandling(authorize -> authorize.authenticationEntryPoint(new HttpStatusEntryPoint(
                HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/auth").permitAll()
                .requestMatchers("/api/v1/user/**").permitAll()
                .anyRequest().authenticated()
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public UserDetailsService customUserDetailsService(final UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Username: " + email + " not found"));
    }

    @Bean
    public AuthenticationManager customAuthenticationManager(UserDetailsService userDetailsService) {
        return authentication -> {
            final String username = authentication.getPrincipal() + "";
            final String password = authentication.getCredentials() + "";

            final UserDetails user = userDetailsService.loadUserByUsername(username);
            if (!password.equals(user.getPassword())) {
                throw new BadCredentialsException("Bad credentials");
            }

            if (!user.isEnabled()) {
                throw new DisabledException("User account is not active");
            }

            return new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
        };
    }
}
