package com.microservice.gateway.config;

import com.microservice.gateway.util.JwtVericator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;


@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Autowired
    private JwtVericator jwtVericator;

    public AuthFilter() {
        super(Config.class);
    }
    private final List<String> bypassPaths = Arrays.asList("/auth/log-in", "/auth/sign-in"); // Add more paths as needed
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public static class Config {
        // Config properties can go here
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try{
                System.out.println("GATEWAY");
                String requestPath = exchange.getRequest().getPath().toString();
                System.out.println("Path: " + requestPath);
                boolean isBypassPath = bypassPaths.stream().anyMatch(path -> pathMatcher.match(path, requestPath));
                String internalHeader = exchange.getRequest().getHeaders().getFirst("X-Internal-Request");

                if (isBypassPath || internalHeader != null) {
                    return chain.filter(exchange);
                }
                String token = extractToken(exchange.getRequest().getHeaders());
                System.out.println("Token: " + token);
                // Validate the token using JwtVericator
                if (token != null && jwtVericator.validateToken(token)) {
                    // If token is valid, proceed with the request
                    return chain.filter(exchange);
                } else {
                    // If token is invalid, return an error response
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    System.out.println("ENTRO AL ERROR DEL GATEWAAY");
                    return exchange.getResponse().setComplete();
                }
            }catch (Exception e){
                e.printStackTrace();
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}