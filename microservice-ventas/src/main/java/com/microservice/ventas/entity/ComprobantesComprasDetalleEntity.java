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
@Table(name = "comprobantes_compras_detalles")
public class ComprobantesComprasDetalleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_comprobante_cabecera")
    private ComprobantesComprasCaEntity comprobanteCabeceraEntity;
    private Long numero;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(nullable = false, name = "id_producto")
    private Long idProducto;
    @Column(precision = 10, scale = 2)
    private BigDecimal peso;
    @Column(precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    @Column(nullable = false)
    private String descripcion;
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
