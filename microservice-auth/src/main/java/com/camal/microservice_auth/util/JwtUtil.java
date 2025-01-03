package com.camal.microservice_auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.camal.microservice_auth.controller.dto.AuthLoginComplete;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;


import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt-key.password}")
    private String privatekey;
    @Value("${jwt-key.username}")
    private String userGenerator;

    public String generateJwtToken(Authentication authentication, String usercodigo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 5);
        Algorithm algorithm = Algorithm.HMAC256(this.privatekey);

        String username = authentication.getPrincipal().toString();

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jwtToken = JWT.create()
                .withIssuer(userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withClaim("usercodigo", usercodigo)
                .withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime())
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
        return jwtToken;
    }

    public DecodedJWT validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(this.privatekey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;

        }catch (JWTVerificationException exception){
            throw new JWTVerificationException("Token invalid, not Authorized");
        }
    }
    public String generateJwtTokenComplete(Authentication authentication, AuthLoginComplete authLoginComplete) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 5);
        Algorithm algorithm = Algorithm.HMAC256(this.privatekey);

        String username = authentication.getPrincipal().toString();

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jwtToken = JWT.create()
                .withIssuer(userGenerator)
                .withSubject(username)
                .withClaim("usercodigo", authLoginComplete.usercodigo())
                .withClaim("authorities", authorities)
                .withClaim("sesionEmpId", authLoginComplete.idEmpresa())
                .withClaim("sesionAlmacenId", authLoginComplete.idAlmacen())
                .withClaim("sesionPuntoVentaId", authLoginComplete.idPuntoVenta())
                .withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime())
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
        return jwtToken;
    }
    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }
    public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}
