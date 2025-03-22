package br.com.bix.images.api.rest.controller;

import br.com.bix.images.api.rest.model.AuthRequest;
import br.com.bix.images.api.rest.model.UserResponse;
import br.com.bix.images.config.security.JwtTokenProvider;
import br.com.bix.images.util.EncryptUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<UserResponse> auth(@RequestBody AuthRequest authRequest) {
        try {
            authRequest.setPass(EncryptUtil.encrypt(authRequest.getPass()));
            Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(authRequest.getEmail(),
                    authRequest.getPass());
            Authentication authentication = this.authenticationManager.authenticate(
                authenticationRequest);
            final String token = jwtTokenProvider.createToken(authentication);

            final UserResponse userResponse = UserResponse.builder()
                .email(authRequest.getEmail())
                .token(token)
                .build();
            return ResponseEntity.ok().body(userResponse);
        } catch(RuntimeException | JsonProcessingException ex) {
            throw new BadCredentialsException("Usuario ou senha inválidos");
        }
    }

}
