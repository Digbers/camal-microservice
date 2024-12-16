package com.microservice.empresas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    private String logosDir;
    public String getLogosDir() {
        if (logosDir == null || logosDir.isEmpty()) {
            // Calcula la ruta actual de ejecución
            return System.getProperty("user.dir") + "/logos";
        }
        return logosDir;
    }
    public void setLogosDir(String logosDir) {
        this.logosDir = logosDir;
    }
}

