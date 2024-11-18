package com.microservice.empresas.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "documentos_tipos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"empresa_id", "doc_codigo"})
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentoTiposEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;  // Nueva clave primaria única

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;

    @Column(name = "doc_codigo", unique = true, nullable = false, length = 3)
    private String docCodigo;

    @Column(name = "doc_descripcion", nullable = false, length = 100)
    private String descripcion;

    @Column(name = "codigo_sunat", nullable = false, length = 3)
    private String codigoSunat;

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
