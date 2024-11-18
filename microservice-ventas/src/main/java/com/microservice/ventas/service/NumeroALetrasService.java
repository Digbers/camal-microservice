package com.microservice.ventas.service;

import org.springframework.stereotype.Service;

@Service
public class NumeroALetrasService {

    private final String[] UNIDADES = { "", "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE" };
    private final String[] DECENAS = { "", "DIEZ", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA" };
    private final String[] CENTENAS = { "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS" };

    public String convertirNumeroALetras(double numero, String moneda) {
        int parteEntera = (int) Math.floor(numero);
        int fraccion = (int) Math.round((numero - parteEntera) * 100);
        String textoNumero = convertirParteEntera(parteEntera);

        return textoNumero + " CON " + (fraccion < 10 ? "0" + fraccion : fraccion) + "/100 " + moneda;
    }

    private String convertirParteEntera(int numero) {
        if (numero == 0) {
            return "CERO";
        }

        StringBuilder resultado = new StringBuilder();
        int terna = 1;

        while (numero > 0) {
            int centenas = numero / 100 % 10;
            int decenas = numero / 10 % 10;
            int unidades = numero % 10;

            String parte = convertirCentenas(centenas, decenas, unidades);

            if (terna == 2 && !parte.isEmpty()) {
                parte += " MIL";
            } else if (terna == 3 && !parte.isEmpty()) {
                parte += " MILLONES";
            }

            resultado.insert(0, parte + " ");
            numero /= 1000;
            terna++;
        }

        return resultado.toString().trim();
    }

    private String convertirCentenas(int centenas, int decenas, int unidades) {
        StringBuilder resultado = new StringBuilder();

        if (centenas == 1 && decenas == 0 && unidades == 0) {
            return "CIEN";
        } else {
            resultado.append(CENTENAS[centenas]).append(" ");
        }

        if (decenas == 1) {
            switch (unidades) {
                case 0: resultado.append("DIEZ"); break;
                case 1: resultado.append("ONCE"); break;
                case 2: resultado.append("DOCE"); break;
                case 3: resultado.append("TRECE"); break;
                case 4: resultado.append("CATORCE"); break;
                case 5: resultado.append("QUINCE"); break;
                default: resultado.append("DIECI").append(UNIDADES[unidades]);
            }
        } else if (decenas == 2 && unidades == 0) {
            resultado.append("VEINTE");
        } else if (decenas == 2) {
            resultado.append("VEINTI").append(UNIDADES[unidades]);
        } else if (decenas > 2) {
            resultado.append(DECENAS[decenas]);
            if (unidades > 0) {
                resultado.append(" Y ").append(UNIDADES[unidades]);
            }
        } else {
            resultado.append(UNIDADES[unidades]);
        }

        return resultado.toString().trim();
    }
}