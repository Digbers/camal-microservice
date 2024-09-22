package com.microservice.ventas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "series")
public class SeriesEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "id_empresa")
    private Long idEmpresa;
    @Column(nullable = false, name = "id_punto_venta")
    private Long idPuntoVenta;
    @ManyToOne
    @JoinColumn(name = "id_tipo_comprobante")
    private ComprobantesTiposVentasEntity codigoTipoComprobante;
    private String codigoSerie;
    private Boolean defaultSerie;
    @Column(name = "usercodigo_creacion")
    private String usuarioCreacion;
    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private Timestamp fechaCreacion;
    @Column(name = "usercodigo_actualizacion")
    private String usuarioActualizacion;
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;

}
