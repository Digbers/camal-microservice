package com.microservice.ventas.service.ventas;

import com.microservice.ventas.controller.DTO.ComprobantesTiposVentasDTO;
import com.microservice.ventas.controller.DTO.ComprobantesVentasCabDTO;
import com.microservice.ventas.controller.DTO.SeriesDTO;
import com.microservice.ventas.controller.DTO.VentaRequest;

import java.util.List;

public interface IVentasService {
    ComprobantesVentasCabDTO save(VentaRequest ventaRequest);
    Boolean deleteById(Long id);
    Boolean anularVenta(Long id);
    String getNumeroXSerie(String serie, Long idPuntoVenta);
    List<ComprobantesTiposVentasDTO> getComprobantesTiposVentas(Long idEmpresa);
    List<SeriesDTO> getSeries(String codigoTipoComprobante, Long idPuntoVenta);
}
