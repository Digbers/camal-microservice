package com.microservice.ventas.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conmprobantes_ventas_detalles")
public class ComprobantesVentasDetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comprobante_venta_cab", nullable = false, referencedColumnName = "id")
    private ComprobantesVentasCabEntity comprobanteCabeceraEntity;
    private Long numero;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(nullable = false, name = "id_producto")
    private Long idProducto;
    @Column(nullable = false, name = "id_envase")
    private Long idEnvase;
    @Column(precision = 10, scale = 2)
    private BigDecimal peso;
    @Column(precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    @Column(precision = 10, scale = 2)
    private BigDecimal descuento;
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
