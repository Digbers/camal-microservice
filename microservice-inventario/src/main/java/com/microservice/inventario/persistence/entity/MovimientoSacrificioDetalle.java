package com.microservice.inventario.persistence.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "MovimientoSacrificioDetalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoSacrificioDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMovimientoDetalle")
    private Long idMovimientoDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_movimiento_cabecera", nullable = false)
    private MovimientoSacrificioCabecera movimientoSacrificioCabecera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProductoVivo")
    private ProductosEntity productoVivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProductoProcesado")
    private ProductosEntity productoProcesado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEnvase", nullable = true)
    private EnvaseEntity envase;  // Envase asociado (si aplica)

    @Column(nullable = false)
    private int cantidadPollosSacrificados;  // Cantidad sacrificada para este detalle

    @Column(precision = 10, scale = 2)
    private BigDecimal pesoTotal;  // Peso total de los productos procesados

    // Auditoría
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