package com.microservice.empresas.controller.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntidadDTO {
    private Long idEmpresa;
    private Long id;
    private Long zona;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String documentoTipo;
    private String nroDocumento;
    private String email;
    private String celular;
    private String direccion;
    private String sexo;
    private Boolean estado;
    private String condicion;
    private String usuarioCreacion;
    private Timestamp fechaCreacion;
    private String usuarioActualizacion;
    private Timestamp fechaActualizacion;
    private List<String> entidadesTiposList;
}
