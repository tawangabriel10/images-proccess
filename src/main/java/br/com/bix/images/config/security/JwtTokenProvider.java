package br.com.bix.images.config.security;

import static java.util.stream.Collectors.joining;

import br.com.bix.images.api.rest.model.UserResponse;
import br.com.bix.images.data.model.UserDocument;
import br.com.bix.images.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwt.token}")
    private String valueSecretKey;

    @Value("${app.jwt.token.expiration.time.milliseconds}")
    private long timeValidationMS;

    private SecretKey secretKey;

    private final UserService userService;

    @PostConstruct
    public void init() {
        final String secret = Base64.getEncoder().encodeToString(valueSecretKey.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Authentication authentication) throws JsonProcessingException {
        final String login = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        final Claims claims = Jwts.claims().setSubject(login);
        final UserResponse user = userService.findByEmail(login);

        claims.put("user", new ObjectMapper().writeValueAsString(user));

        if (!authorities.isEmpty()) {
            claims.put("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(joining(",")));
        }

        final Date now = new Date();
        final Date validity = new Date(now.getTime() + timeValidationMS);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(this.secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts
                .parserBuilder().setSigningKey(this.secretKey).build()
                .parseClaimsJws(token);
            log.debug("expiration date: {}", claims.getBody().getExpiration());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody();

        Object authoritiesClaim = claims.get("authorities");

        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
            : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        try {
            final String gsonAuth = (String) claims.get("user");
            final UserDocument userDocument = new ObjectMapper().readValue(gsonAuth, UserDocument.class);
            return new UsernamePasswordAuthenticationToken(userDocument, token, authorities);
        } catch (Exception e) {
            throw new BadCredentialsException("Erro durante autenticação", e);
        }
    }

}
