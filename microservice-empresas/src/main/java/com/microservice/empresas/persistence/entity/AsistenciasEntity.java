package com.microservice.empresas.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asistencias")
public class AsistenciasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entidad_id", referencedColumnName = "id", nullable = false)
    private EntidadEntity entidad;

    @Column(name = "fecha_asistencia", unique = true, nullable = false)
    private LocalDate fechaAsistencia;

    @Column(name = "asistio", nullable = false)
    private Boolean asistio;
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