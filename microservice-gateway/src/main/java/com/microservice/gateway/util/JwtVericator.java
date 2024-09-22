package com.microservice.gateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtVericator {
    @Value("${jwt-key.password}")
    private String privatekey;
    @Value("${jwt-key.username}")
    private String userGenerator;

    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privatekey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token invalid, not Authorized in the system");
        }
    }
}
