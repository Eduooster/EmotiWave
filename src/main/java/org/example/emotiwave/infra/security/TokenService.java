package org.example.emotiwave.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value(value = "${api.security.token.secret}")
    private String secret;

    public String gerarToken(UserDetails usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API EmotiWave")
                    .withSubject(usuario.getUsername())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (Exception exception) {
            throw new RuntimeException("erro ao gerrar token jwt", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("API miniecommerce")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(20).toInstant(ZoneOffset.of("-03:00"));
    }
}
