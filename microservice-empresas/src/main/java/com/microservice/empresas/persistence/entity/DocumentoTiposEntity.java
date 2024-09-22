package com.microservice.empresas.persistence.entity;

import com.microservice.empresas.persistence.entity.ids.DocumentosTiposId;
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
@Table(name = "documentos_tipos")
@IdClass(DocumentosTiposId.class)
public class DocumentoTiposEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;

    @Id
    @Column(name = "doc_codigo", nullable = false, length = 3)
    private String docCodigo;

    @Column(name = "doc_descripcion", nullable = false, length = 100)
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
