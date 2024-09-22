package com.microservice.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class FreePassFilter extends AbstractGatewayFilterFactory<FreePassFilter.Config> {
    public FreePassFilter() {
        super(FreePassFilter.Config.class);
    }
    public static class Config {
        // Config properties can go here
    }
    @Override
    public GatewayFilter apply(FreePassFilter.Config config) {
        return (exchange, chain) -> {
            try{
                System.out.println("GATEWAY");
                String requestPath = exchange.getRequest().getPath().toString();
                System.out.println("Path: " + requestPath);
                return chain.filter(exchange);

            }catch (Exception e){
                e.printStackTrace();
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
}
