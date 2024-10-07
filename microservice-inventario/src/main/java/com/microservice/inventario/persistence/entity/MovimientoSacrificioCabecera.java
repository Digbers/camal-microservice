package com.microservice.inventario.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "MovimientoSacrificioCabecera")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoSacrificioCabecera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento_cabecera")
    private Long idMovimientoCabecera;

    @Column(nullable = false)
    private Long idEmpresa;  // Identificador de la empresa

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDate fechaMovimiento;  // Fecha del movimiento
    private String responsable;  // Usuario encargado del sacrificio
    @Column(nullable = false, name ="cantidad_total_pollos")
    private int cantidadTotalPollosSacrificados;  // Cantidad total de pollos sacrificados en esta operación

    @OneToMany(mappedBy = "movimientoSacrificioCabecera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoSacrificioDetalle> detalles;

    @Column(name = "observaciones", length = 500)
    private String observaciones;  // Información adicional o notas del proceso
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