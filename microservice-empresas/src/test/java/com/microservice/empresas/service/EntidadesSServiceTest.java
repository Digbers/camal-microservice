package com.microservice.empresas.service;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

import com.microservice.empresas.client.EntidadesSClient;
import com.microservice.empresas.controller.dto.PadronSunatDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EntidadesSServiceTest {

    @Mock
    private EntidadesSClient entidadesSClient;

    @InjectMocks
    private EntidadesSService entidadesSService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByNumeroDocTipoDocumento() {
        // Configurar datos de prueba
        String numeroDoc = "123456789";
        String tipoDoc = "FAC"; // Por ejemplo, tipo 1 para RUC
        String urlEsperada = "https://e-consultaruc.sunat.gob.pe/cl-ti-itmrconsruc/FrameCriterioBusquedaWeb.jsp";

        PadronSunatDTO padronMock = new PadronSunatDTO();
        padronMock.setRazonSocial("Empresa de Prueba S.A.");

        // Configurar comportamiento del mock
        when(entidadesSClient.obtenerEntidad(numeroDoc, tipoDoc, urlEsperada)).thenReturn(padronMock);

        // Llamar al método y verificar el resultado
        PadronSunatDTO resultado = entidadesSService.findByNumeroDocTipoDocumento(numeroDoc, tipoDoc);
        assertEquals("Empresa de Prueba S.A.", resultado.getRazonSocial());
    }
}
